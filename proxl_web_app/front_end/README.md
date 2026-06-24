# Proxl Web App — Front End Build

Builds the front-end JS/CSS bundles (Handlebars template precompile + webpack)
and copies them into the web app under:

- `../src/main/webapp/static/js_generated_bundles`
- `../src/main/webapp/static/css_generated`

This runs automatically as part of the parent web app build
(`proxl_web_app`, `./gradlew war`), but can also be run on its own from here.

## Node

The build uses **webpack 5** and the pure-JS dart **`sass`** package, so there is
no native compile step and **no Node version pin** — Node 20 or 24 both work.

Gradle runs the node tools (handlebars, webpack) by invoking `node` from your
`PATH` — there is no Gradle node plugin — so you just need a supported Node active
in the terminal you build from:

    node -v              # 20.x or 24.x

(If you use nvm and your default isn't already 20+, e.g. `nvm use 24`.)

## Build

First time, or after deleting `node_modules`:

    npm install

Then build the front end:

    ./gradlew frontEndBuild

(`frontEndBuild` is the default task, so plain `./gradlew` also works.)

Outputs are written under `../src/main/webapp/static/`.
