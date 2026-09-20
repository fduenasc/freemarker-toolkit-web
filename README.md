# FreeMarker Toolkit Web

Web tool to try FreeMarker templates with JSON data and see the result right away.

## What it does

- Write a FreeMarker template.
- Load a JSON data model.
- Process the template and view the output.
- Format JSON or the template.
- Validate that the output has the fields you expect.
- Adjust UI language, locale, and time zone.

## Requirements

- Java 21 or later.
- Maven (the project includes the `mvnw` wrapper).

## How to run

On Windows:

```powershell
.\mvnw.cmd quarkus:dev
```

On Mac or Linux:

```bash
./mvnw quarkus:dev
```

Then open [http://localhost:8080/](http://localhost:8080/) in your browser.

## How to use it

1. Write the **template** in the left panel.
2. Write the **data model** (JSON) in the right panel.
3. Click **Process template** to see the result.
4. Optionally format the JSON or template with the panel buttons.
5. Under **Expected fields**, define which output paths should exist and validate them.

Quick example:

**Template**
```ftl
Hello ${name}!
```

**Data model**
```json
{
  "name": "Ana"
}
```

**Result**
```text
Hello Ana!
```

## Settings

From the settings menu you can change:

- UI language (Spanish or English).
- FreeMarker locale.
- Time zone.

Preferences are kept for the browser session.

## Package for production

```powershell
.\mvnw.cmd package
java -jar target\quarkus-app\quarkus-run.jar
```

## Code layout

The project follows a simple hexagonal layout:

- `domain/model` → result types and stable message keys.
- `domain/usecase` → business rules, ports, and domain exceptions.
- `application` → coordinates the tool flow.
- `infrastructure/entrypoints/web` → web UI (Vaadin) and i18n text.
- `infrastructure/drivenadapters/freemarker` → FreeMarker engine.
- `infrastructure/helpers` → user preferences.

## License

Licensed under the [Apache License, Version 2.0](LICENSE).
