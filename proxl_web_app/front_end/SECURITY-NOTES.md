# Front-end dependency security notes

Accepted / known npm-audit (Dependabot) findings for `front_end` and why.

## crypto-js < 4.2.0 — accepted (unreachable)

- **Advisory:** GHSA-xwcq-pm8m-c4vf — crypto-js PBKDF2 far weaker than the current
  standard. Severity: critical.
- **How it gets here:** transitively, **only** via `pdfkit` (pdfkit bundles
  crypto-js for *encrypted* PDF output). It is not a direct dependency.
- **Why it does not apply:** the weak PBKDF2 code path is reached only when
  generating a **password-encrypted** PDF. The single place pdfkit is used —
  `src/js/page_js/data_pages/project_search_ids_driven_pages/common/svgDownloadUtils.js` —
  creates PDFs with `new PDFDocument({ compress: false })` and **no**
  `password` / `ownerPassword` / `userPassword` option, so no encryption (and
  thus no PBKDF2) ever runs. The vulnerable code is unreachable in this app.
- **Re-evaluate if:** PDF encryption is ever added (any `*Password` option to
  `PDFDocument`), or pdfkit gains another caller.
- **Permanent fix (when convenient):** upgrade `pdfkit` to a version that no
  longer depends on crypto-js. That is a breaking pdfkit major bump and would
  require re-testing the image/structure-page "download as PDF" path (pdfkit
  runs in the browser via webpack node-core polyfills), so it is deferred.
