# Copy Requests Safely

When reporting a bug or a triager responding to a report, one shouldn't include headers which contain access tokens and cookies. I used to manually remove them, so to solve that problem, this extension was born.

Burp Extension for copying requests safely. 

It redacts headers:
Basic headers are being redacted right now by this extension. These include the common ones:

- Authorization:
- Cookie
- X-CSRF-Token

More support can and will be added in the future
