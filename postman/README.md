## Postman Collection

### Import Collection

1. Download `sports-venue-booking.postman_collection.json`
2. Open Postman
3. Click **Import** → **Upload Files**
4. Select the collection file
5. Collection will appear with all endpoints organized in folders

### Environment Setup

The collection uses a `baseURL` variable set to `http://localhost:8080` by default.

To change:
1. Click on collection name
2. Go to **Variables** tab
3. Update `baseURL` value

### Test Flow

Follow this order for complete testing:

**1. Sports (Auto-synced on startup)**
- Get All Sports - Verify 5 sports exist

**2. Create Test Data**
- Create 3-4 venues using the sample requests
- Create 4-5 slots with different sports and times
- Note the IDs returned

**3. Test Availability**
- Search all sports for full day
- Search specific sport for morning/evening
- Verify results match created slots

**4. Test Booking Flow**
- Create booking for slot 1 ✅
- Try to book same slot again ❌ (should fail)
- Cancel booking
- Rebook same slot ✅

**5. Error Cases**
- Run all requests in "Error Test Cases" folder
- All should return 4xx errors

### Sample Test Sequence
```
1. POST /api/sports/sync
2. POST /api/venues (x3)
3. POST /api/venues/1/slots (x4)
4. GET /api/venues/available
5. POST /api/bookings
6. PUT /api/bookings/1/cancel
```