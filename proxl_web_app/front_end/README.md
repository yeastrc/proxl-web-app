# Proxl Web App — Front End Build

Builds the front-end JS/CSS bundles (Handlebars template precompile + webpack)
and copies them into the web app under:

- `../src/main/webapp/static/js_generated_bundles`
- `../src/main/webapp/static/css_generated`

This runs automatically as part of the parent web app build
(`proxl_web_app`, `./gradlew war`), but can also be run on its own from here.

## Requires Node 20 (do NOT use a newer Node)

The build uses **webpack 4** and **node-sass 9**, which need **Node.js 20**.
Do **not** use Node 22/24: `node-sass 9` has no prebuilt binary for those, so a
fresh `npm install` will fail trying to compile it from source.

Gradle runs the node tools (handlebars, webpack) by invoking `node` from your
`PATH` — there is no Gradle node plugin — so you only need Node 20 active in the
terminal you build from.

### Make Node 20 active

Using nvm (recommended):

    nvm install 20       # once, if not already installed
    nvm use 20
    node -v              # confirm: v20.x

Or, without loading nvm, prepend a specific Node 20 to PATH for the session:

    export PATH="$HOME/.nvm/versions/node/v20.20.2/bin:$PATH"
    node -v              # confirm: v20.x

Or run a single Gradle command under Node 20 without changing the shell:

    nvm exec 20 ./gradlew frontEndBuild

## Build

First time, or after deleting `node_modules`:

    npm install

Then build the front end:

    ./gradlew frontEndBuild

(`frontEndBuild` is the default task, so plain `./gradlew` also works.)

Outputs are written under `../src/main/webapp/static/`.
