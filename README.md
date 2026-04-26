# Billing Automation

This repository contains a Java Spring Boot backend scaffold for billing automation, plus sample Couchbase documents used for testing.

## GitHub Pages

A static website is available from the `docs/` folder. To publish it:

1. Open repository Settings on GitHub.
2. Go to Pages.
3. Select the `main` branch.
4. Choose `/docs` as the folder.
5. Save.

After publishing, the site should be available at:

`https://gattem.github.io/Billing_Automation/`

## Local Couchbase configuration

The backend is configured to connect to your local Couchbase instance with these settings:

```properties
couchbase.host=localhost
couchbase.bucket=sessiondb_billing
couchbase.username=mahesh
couchbase.password=admin123
```

You can update these values in `src/main/resources/application.properties` if needed.

## Landing page preview

The `docs/index.html` page now shows a basic landing page and a mock UI preview for the automation flow.
