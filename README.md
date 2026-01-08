# Sports Venue Booking System

A REST API service for managing sports venue bookings with real-time availability checking and double-booking prevention.

## Tech Stack

- Java 21
- Spring Boot 3.2.1
- MySQL 8.0
- Docker & Docker Compose
- Maven

## Getting Started

### Prerequisites

- Docker and Docker Compose installed
- Ports 8080 and 3307 available

### Running the Application
```bash
# Clone the repository
git clone <repository-url>
cd sports-venue-booking

# Start services
docker-compose up --build

# Application will start at http://localhost:8080
# MySQL will be available at localhost:3307
```

The application automatically fetches sports data from the external API on startup.

### Stopping the Application
```bash
# Stop services
docker-compose down

# Clean database (fresh start)
docker-compose down -v
```

## API Endpoints

### Core APIs (As Per Requirements)
```
POST   /venues                      - Create a venue
GET    /venues                      - List all venues
POST   /venues/{venueId}/slots      - Add time slot to venue
GET    /venues/available            - Search available venues
POST   /bookings                    - Book a slot
PUT    /bookings/{id}/cancel        - Cancel a booking
```

### Additional Helper APIs
```
GET    /api/sports                  - List available sports
GET    /api/venues/{id}             - Get venue details
GET    /api/slots/{id}              - Get slot details
GET    /api/bookings/{id}           - Get booking details
```

## Key Design Decisions

### 1. Sports Data Management

**Problem:** Assignment requires sports to come from external API, but the API doesn't provide descriptions or allow filtering.

**Solution:** 
- Fetch sports on application startup
- Store in database with `sport_id` and `sport_code` as required
- Added `lastSyncedAt` timestamp to track freshness
- Manual sync endpoint available if needed

**Why this approach:**
- Reduces dependency on external API during runtime
- Faster API responses (no external call per request)
- Allows offline development and testing

### 2. Venue-Sport Relationship

**Problem:** How to handle multi-sport venues? Can a venue host cricket and football simultaneously?

**Decision:** YES - One venue can host multiple sports at the same time.

**Reasoning:**
- Real-world venues have separate grounds (cricket field, football pitch, tennis courts)
- More booking opportunities = better user experience
- Slot overlap checking is per venue + per sport combination

**Implementation:**
```
Andheri Sports Complex
├── Cricket Ground: 6-8 AM (Slot 1)
├── Football Field: 6-8 AM (Slot 2)   Allowed - different sports
└── Cricket Ground: 6-8 AM (Slot 3)   Blocked - same sport overlaps
```

### 3. Double Booking Prevention

**Challenge:** Two users trying to book the same slot simultaneously.

**Solution: Pessimistic Locking**
```java
@Transactional
public BookingResponse createBooking(BookingRequest request) {
    // Lock the slot row in database
    Slot slot = slotRepository.findByIdForUpdate(slotId);
    
    if (slot.isBooked()) {
        throw new SlotAlreadyBookedException();
    }
    
    slot.setIsBooked(true);
    // Save booking
}
```

**How it works:**
- `findByIdForUpdate()` uses `SELECT ... FOR UPDATE`
- Database locks the row until transaction completes
- Second user waits, then sees slot is already booked
- Zero chance of double booking

**Why not optimistic locking?**
- Optimistic locking can fail and require retry
- Pessimistic locking is simpler and deterministic
- Performance impact is negligible for booking operations

### 4. Slot Overlap Detection

**Problem:** Prevent overlapping slots for the same sport at the same venue.

**Algorithm:**
```java
// Two time ranges overlap if:
// (start1 < end2) AND (end1 > start2)

List<Slot> overlaps = repository.findOverlappingSlots(
    venueId, 
    sportId,  // Check same sport only
    startTime, 
    endTime
);
```

**Why this works:**
- Handles all overlap scenarios (partial, complete, containment)
- Database-level check before inserting
- Indexed query for fast lookup

### 5. Booking History vs Active Bookings

**Problem:** Should we maintain booking history or just current bookings?

**Decision:** Maintain full history with status tracking.

**Database Design:**
```
bookings table:
- id (PK)
- slot_id (FK, NOT unique)  ← Allows multiple bookings per slot
- status (CONFIRMED/CANCELLED)
- customer details
- timestamps

slots table:
- is_booked (boolean)  ← Controls availability
```

**Why:**
- Audit trail for business analytics
- Know who booked/cancelled
- Handle disputes or refunds
- Track cancellation patterns

**How it prevents double booking:**
- Business logic checks `is_booked` flag on slot
- Database allows historical records but logic prevents simultaneous bookings

### 6. Slot Immutability

**Requirement:** "Slot time is immutable once booked"

**Implementation:**
```java
public void updateSlot(Long id, SlotRequest request) {
    Slot slot = findById(id);
    
    if (slot.isBooked()) {
        throw new Exception("Cannot modify booked slot");
    }
    
    // Allow update only if not booked
}
```

**Lifecycle:**
```
Created → Can update/delete
Booked → Cannot update/delete (IMMUTABLE)
Booking Cancelled → Can update/delete again
```

### 7. Venue Deletion Safety

**Problem:** What if someone tries to delete a venue with active slots?

**Solution:** Prevent deletion if slots exist.
```java
public void deleteVenue(Long id) {
    long slotCount = slotRepository.countSlotsByVenue(id);
    
    if (slotCount > 0) {
        throw new Exception("Cannot delete venue with slots");
    }
    
    // Delete only if empty
}
```

**Why:**
- Prevents accidental data loss
- Forces explicit cleanup
- Maintains referential integrity

## Database Schema

### Key Tables

**sports**
```sql
- id (PK, auto-increment)
- sport_id (unique, from external API)
- sport_code (from external API)
- sport_name
- is_active
- last_synced_at
```

**venues**
```sql
- id (PK)
- name (unique)
- location
- contact_phone
- contact_email
- is_active
```

**slots**
```sql
- id (PK)
- venue_id (FK)
- sport_id (FK)
- start_time
- end_time
- price
- is_booked (controls availability)
```

**bookings**
```sql
- id (PK)
- slot_id (FK, not unique - allows history)
- customer_name
- customer_email
- customer_phone
- booking_date
- status (CONFIRMED/CANCELLED)
```

### Indexes Created
```sql
-- Slot search optimization
CREATE INDEX idx_slot_venue_time ON slots(venue_id, start_time, end_time);
CREATE INDEX idx_slot_search ON slots(sport_id, start_time, end_time, is_booked);

-- Booking lookups
CREATE INDEX idx_booking_customer_email ON bookings(customer_email);
CREATE INDEX idx_booking_slot_status ON bookings(slot_id, status);

-- Venue search
CREATE INDEX idx_venue_name ON venues(name);
```

## Business Rules & Assumptions

### Assumptions Made

1. **Guest Bookings**
   - No user authentication required
   - Customer details stored per booking
   - Email used as identifier for retrieving bookings

2. **Venue Capacity**
   - Each venue can host multiple sports simultaneously
   - Different sports = different physical spaces

3. **Booking Windows**
   - Slots must be at least 30 minutes
   - Maximum slot duration is 24 hours
   - Cannot book past slots
   - Cannot cancel after slot start time

4. **Pricing**
   - Price set at slot level
   - No dynamic pricing or discounts
   - Price stored as BigDecimal for precision

5. **Time Zones**
   - All times stored in Asia/Kolkata timezone
   - No timezone conversion needed

6. **Data Persistence**
   - Single MySQL instance
   - No caching layer
   - Database handles all consistency

### Validation Rules

**Slots:**
- Start time must be before end time
- Start time must be in future
- Duration: 30 minutes minimum, 24 hours maximum
- Cannot overlap with same sport at same venue

**Bookings:**
- Slot must be available (not already booked)
- Slot must be in future
- Customer email must be valid format

**Venues:**
- Name must be unique
- Cannot delete if slots exist

## Sample API Calls

### 1. Sync Sports (Optional - Auto-runs on startup)
```bash
curl -X POST http://localhost:8080/api/sports/sync
```

### 2. Create Venue
```bash
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Andheri Sports Complex",
    "location": "Andheri East, Mumbai, Maharashtra",
    "contactPhone": "+91-9876543210",
    "contactEmail": "contact@andheri.com"
  }'
```

### 3. Add Time Slot
```bash
curl -X POST http://localhost:8080/api/venues/1/slots \
  -H "Content-Type: application/json" \
  -d '{
    "sportId": 3,
    "startTime": "2026-01-10T06:00:00",
    "endTime": "2026-01-10T08:00:00",
    "price": 2000.00
  }'
```

### 4. Search Available Venues
```bash
# All sports
curl "http://localhost:8080/api/venues/available?startTime=2026-01-10T00:00:00&endTime=2026-01-10T23:59:59"

# Cricket only
curl "http://localhost:8080/api/venues/available?sportId=3&startTime=2026-01-10T06:00:00&endTime=2026-01-10T18:00:00"
```

### 5. Book a Slot
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "customerName": "Hardik Sharma",
    "customerEmail": "hardik@example.com",
    "customerPhone": "+91-9876543210"
  }'
```

### 6. Cancel Booking
```bash
curl -X PUT http://localhost:8080/api/bookings/1/cancel
```

## Testing Double Booking Prevention

**Scenario:** Two users try to book the same slot simultaneously.
```bash
# Terminal 1
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "customerName": "User A",
    "customerEmail": "usera@example.com",
    "customerPhone": "+91-9999999999"
  }'

# Terminal 2 (at the same time)
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "slotId": 1,
    "customerName": "User B",
    "customerEmail": "userb@example.com",
    "customerPhone": "+91-8888888888"
  }'
```

**Result:**
- User A: 201 Created 
- User B: 409 Conflict - "Slot is already booked" 



## Error Handling

All errors return consistent format:
```json
{
  "success": false,
  "message": "Detailed error message",
  "error": "ERROR_CODE",
  "timestamp": "2026-01-08T10:00:00"
}
```

**Common Error Codes:**
- `SLOT_OVERLAP` - Overlapping time slots
- `SLOT_ALREADY_BOOKED` - Double booking attempt
- `INVALID_SLOT_TIME` - Time validation failed
- `NOT_FOUND` - Resource doesn't exist
- `VALIDATION_ERROR` - Invalid input data

## Project Structure
```
src/main/java/com/venue/sports_venue_booking/
├── config/          - Configuration classes
├── controller/      - REST API endpoints
├── dto/             - Request/Response objects
│   ├── request/
│   └── response/
├── entity/          - Database entities
├── exception/       - Custom exceptions
├── repository/      - Database access
├── service/         - Business logic
│   └── impl/
└── util/            - Helper classes
```

## Development

### Local Development (Without Docker)
```bash
# Start MySQL only
docker-compose -f docker-compose-dev.yml up -d

# Run Spring Boot
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

### Access Database
```bash
docker exec -it venue-booking-mysql mysql -uroot -proot123 venue_booking
```

### Rebuild Application
```bash
# Clean rebuild
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests
```

## Known Limitations

1. **No Authentication**
   - Anyone can create/cancel bookings
   - Suitable for assignment scope

2. **No Payment Integration**
   - Bookings are free
   - Price is informational only

3. **No Email Notifications**
   - No confirmation emails sent
   - Would require SMTP setup

4. **Single Instance**
   - Not designed for horizontal scaling
   - Database is single point of failure

5. **Basic Availability Search**
   - No filtering by price range
   - No venue ratings or reviews

## Future Enhancements

1. User authentication with roles (admin/customer)
2. Payment gateway integration
3. Email notifications for bookings
4. Review and rating system
5. Booking modification (not just cancel)
6. Recurring slots (daily/weekly patterns)
7. Venue photos and descriptions
8. Distance-based search
9. Peak/off-peak pricing
10. Waitlist for fully booked slots

## Contact

**Anish**
- Email: anishjaiswal381@gmail.com
- GitHub: [GitHub](https://github.com/AnishJaiswal4444)
- LinkedIn: [LinkedIn](https://www.linkedin.com/in/anishjaiswal4444/)
