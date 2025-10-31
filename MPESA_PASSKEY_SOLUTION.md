# M-Pesa Pass Key Solution Guide

Since you can't find the Pass Key in the Developer Portal, here are your options:

## Option 1: Enable "Lipa na M-Pesa Online" API

The Pass Key is usually revealed when you enable the STK Push API:

1. Go to **MY APPS** in the Safaricom Developer Portal
2. Click on your **"School Management"** app
3. Look for **"API Products"** or **"APIs"** section
4. Find **"Lipa na M-Pesa Online"** or **"M-Pesa Express (STK Push)"**
5. **Enable/Activate** this API if it's not already enabled
6. After enabling, click on it or go to its settings
7. The Pass Key should now be visible in the credentials

## Option 2: Check API Settings Directly

Sometimes the Pass Key is in a different location:

1. **MY APPS** → Your App → **"APIs"** tab
2. Look for **"Lipa na M-Pesa Online"** 
3. Click on it to expand
4. Look for **"Credentials"**, **"Settings"**, or **"View Details"**
5. The Pass Key might be listed there

## Option 3: Generate Pass Key (If Available)

Some developer portals allow you to generate a Pass Key:

1. In your app settings, look for **"Generate Pass Key"** button
2. Or **"Create Pass Key"** in Security settings
3. Generate and copy it immediately (it might only show once)

## Option 4: Contact Safaricom Support

If none of the above work:

1. Go to the Developer Portal
2. Look for **"Support"** or **"Contact Us"** section
3. Create a support ticket asking for:
   - Your M-Pesa Sandbox Pass Key
   - Or instructions on how to access it
4. They can also help enable the API if needed

## Option 5: Test Without Pass Key (For Credentials Verification)

I've updated the code to allow testing your Consumer Key/Secret without the Pass Key. This will help verify your credentials work before you find the Pass Key.

### Test Your Credentials Now:

```bash
# Set your credentials
export MPESA_CONSUMER_KEY="17ZdF9Q2PKAOwJ64Yzl1dxS4iZMsQOAXUUaEVSQZrTmDbMkG"
export MPESA_CONSUMER_SECRET="ZGhNJhzbE9DHKKmAhAZWI045A5B6yUG3uAqnXpx5LJjXYWH7Stn8xUxqGHWpfhKE"
export MPESA_SHORT_CODE="174379"

# For now, use a placeholder (you'll get an error, but you can verify credentials work)
export MPESA_PASS_KEY="placeholder"

# Start your app
mvn spring-boot:run
```

Then try to initiate STK Push. You'll get a Pass Key error, but first check the logs:
- If you see "Failed to get access token" → Your Consumer Key/Secret might be wrong
- If you see "M-Pesa pass key not configured" → Your credentials WORK! You just need the Pass Key

## Option 6: Alternative - Use Test Credentials

Some developers report that for sandbox testing, you can use:
- **Pass Key**: Sometimes it's just an empty string or a default value
- But this is rare and not recommended

## Recommended Next Steps

1. ✅ **First**: Try Option 1 (Enable "Lipa na M-Pesa Online" API)
2. ✅ **Then**: Try Option 2 (Check API Settings)
3. ✅ **If still not found**: Contact Safaricom Support (Option 4)

## Quick Test Script

I've created a test script that will help verify your credentials work:

```bash
./test-mpesa-credentials.sh
```

This will test if your Consumer Key/Secret can get an access token (this works without Pass Key).

