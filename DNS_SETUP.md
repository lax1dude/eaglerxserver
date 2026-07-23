# DNS Configuration for play.cecilmc.net
# Add these records to your domain registrar's DNS settings

## A Record (Primary)
```
Type: A
Name: play
TTL: 3600
Value: 100.55.67.89
```

## AAAA Record (IPv6 - Optional)
```
Type: AAAA
Name: play
TTL: 3600
Value: [Your IPv6 address if available]
```

## Instructions by Registrar:

### GoDaddy
1. Login to GoDaddy account
2. Go to "Manage My Domains"
3. Click on `cecilmc.net`
4. Click "DNS" tab
5. Find or add A record for `play`
6. Set value to `100.55.67.89`
7. Save changes

### Namecheap
1. Login to Namecheap
2. Go to "Dashboard" > "Domain List"
3. Click "Manage" on `cecilmc.net`
4. Go to "Advanced DNS" tab
5. Add/edit A record for `play` with IP `100.55.67.89`
6. Save

### Other Registrars
1. Find the DNS/Domain Settings
2. Add or edit A record
3. Host: `play`
4. Value: `100.55.67.89`
5. TTL: 3600 (or default)
6. Save changes

## Verification
After changes propagate (5-30 minutes), test with:
```bash
nslookup play.cecilmc.net
ping play.cecilmc.net
```

Should resolve to: `100.55.67.89`

## Important Notes
- Changes can take 5 minutes to 48 hours to fully propagate
- Clear your local DNS cache if it doesn't work immediately
- Ensure your firewall allows traffic on Eaglercraft port (8080/8443)
