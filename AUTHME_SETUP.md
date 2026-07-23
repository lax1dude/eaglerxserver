# AuthMe Setup Guide

## Overview
AuthMe provides password-protected login for your Eaglercraft server. Players must register and login with a password before playing.

## Server Owner Account

### Username: `cecilthecreator`
### Password Setup Required ✓

**To set the owner password:**

1. **First time joining the server:**
   - Join with username: `cecilthecreator`
   - Run: `/register <your_password> <your_password>`
   - Example: `/register MySecurePass123 MySecurePass123`

2. **Logging in after that:**
   - Run: `/login <your_password>`
   - Example: `/login MySecurePass123`

## Player Commands

### For New Players
```
/register <password> <confirmPassword>
```
Registers a new account with AuthMe.

### For Returning Players
```
/login <password>
```
Logs into their account.

### For Password Changes
```
/changepassword <oldPassword> <newPassword>
```
Changes their account password.

## Admin Commands

With OP status, `cecilthecreator` can use these commands:

```
/authme reload                  - Reload AuthMe configuration
/authme purge <player>         - Delete a player's account
/authme changepassword <player> - Change another player's password
/authme setpassword <player>   - Set a new password for player
```

## Configuration Features

✓ **BCRYPT Encryption** - Secure password hashing
✓ **Minimum Password Length** - 6 characters required
✓ **Session Timeout** - 5 minutes of inactivity
✓ **Spawn Protection** - Players teleport to spawn on first login
✓ **Interaction Protection** - Prevent griefing before login
✓ **SQLite Database** - Built-in, no external database needed

## Important Notes

1. **Set your password immediately** when you first join
2. **Passwords are case-sensitive**
3. **Remember your password** - Only you can change it if forgotten
4. **Logout command** - Use `/logout` when leaving (optional)
5. **Unregister** - Players can use `/unregister <password>` to delete their account

## Database

AuthMe uses SQLite by default, stored in:
```
plugins/AuthMe/authme.db
```

No external database setup needed!

## Troubleshooting

### Forgot your password?
- Only server admins can reset it using: `/authme setpassword cecilthecreator newpassword`

### Players can't login?
- Check the server logs for errors: `docker-compose logs eaglercraft-server`
- Verify AuthMe plugin is loaded: `/plugins`

### Want to disable login requirement?
- Edit `plugins/AuthMe/config.yml` and set `force: false` under registration

---

**Your server is now password-protected and ready!** 🔐
