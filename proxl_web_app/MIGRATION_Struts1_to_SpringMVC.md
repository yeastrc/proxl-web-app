# Proxl Web App — Struts 1 → Spring Boot / Spring MVC Migration Analysis

Goal: migrate the **Struts 1 layer only** to Spring Boot / Spring MVC while
**keeping the Jersey JAX-RS web services** and **staying on the same servlet
spec** (Servlet 3.1, `javax.*` namespace).

This document is an analysis / plan. No code has been changed.

---

## 1. Current architecture inventory

| Layer | Current implementation | Count |
|---|---|---|
| Front controller | Struts 1.2.9 `ActionServlet`, mapped to `*.do` | 1 |
| Action classes | All `extends org.apache.struts.action.Action` (no `DispatchAction`) | 79 |
| Form beans | `extends ActionForm`, thin (max ~8 fields) | 9 |
| Action mappings | `WEB-INF/struts-config.xml` | ~80 |
| JSP views | total / using Struts taglibs | 123 / 65 |
| Web services | **Jersey JAX-RS** at `/services/*` (`@Path`) — KEEP | 125 |
| Plain servlets | `SharePageURLHandlerServlet` (`/go`), `ProjectLabelServlet` (`/p/*`) — KEEP | 2 |
| Filters | `SetCharacterEncodingFilter`, `SSOSessionKeepAliveServletFilter`, `InitialServletFilter` — KEEP | 3 |
| Listener | `ServletContextAppListener` — KEEP | 1 |

Runtime / build:

- **Servlet 3.1**, `javax.servlet` namespace
- Tomcat 8.5 (`tomcat-embed-core` provided), Java 8, WAR packaging via Gradle
- `spring-security-crypto 5.1.5` already on the classpath (Spring 5.x line already partly present)

### Struts API surface actually used (small & consistent)

```
112  org.apache.struts.action.ActionForm
108  org.apache.struts.action.ActionMapping
108  org.apache.struts.action.Action
 75  org.apache.struts.action.ActionForward
  4  org.apache.struts.action.ActionMessages
  4  org.apache.struts.action.ActionMessage
  2  org.apache.struts.action.ActionErrors
```

No `DispatchAction` / `LookupDispatchAction`, no Struts Validator framework, no
Tiles. This keeps the Java-side conversion mechanical.

---

## 2. The controlling constraint: servlet spec → Spring version ceiling

The app is **Servlet 3.1 on the `javax.servlet` namespace**. This hard-caps the
Spring version:

- ✅ **Spring Framework 5.3 / Spring Boot 2.7.x** — last line on `javax.*` +
  Servlet 3.1/4.0. **This is the target.**
- ❌ Spring Boot 3 / Spring 6 — require `jakarta.*` and Servlet 5+. That would
  force rewriting Jersey, all 3 filters, both servlets, the listener, and every
  `import javax.servlet.*` — explicitly **not** "the same servlet spec."

### Packaging recommendation

Keep **WAR-on-Tomcat** packaging (not Spring Boot's embedded server) so the
Jersey servlet, the 2 plain servlets, the 3 filters, and the listener keep their
exact current registration and the servlet spec is genuinely unchanged. Spring
Boot 2.7 can still provide config via `spring-boot-starter-web` +
`spring-boot-starter-tomcat` as `providedRuntime`.

---

## 3. What stays untouched

Jersey (`/services/*`, 125 `@Path` classes), the 2 plain servlets, all 3
filters, and the listener are standard Servlet-spec components. They coexist
with Spring's `DispatcherServlet` without change. **Only the Struts pieces
migrate.**

---

## 4. Migration mapping

| Struts 1 construct | Spring MVC 5.3 equivalent |
|---|---|
| `ActionServlet` + `*.do` | `DispatcherServlet` mapped to `*.do` (keep extension → URLs unchanged, no client/JS changes) |
| `extends Action` / `execute(...)` | `@Controller` class + `@RequestMapping("/listProjects.do")` method |
| `ActionForward "Success"` → JSP | `return "viewName"` + `InternalResourceViewResolver` (`/WEB-INF/jsp-pages/`) |
| `ActionForward` redirect | `return "redirect:/listProjects.do"` |
| `ActionForm` (9 beans) | `@ModelAttribute` POJOs (drop `extends ActionForm`; keep getters/setters — binding is identical) |
| `mapping.getParameter()` dispatch (8 actions) | Distinct `@RequestMapping` methods, or `params=` / path differentiation |
| `global-forwards` (login, error, …) | Constants → shared redirect strings / `@ExceptionHandler` |
| `ActionMessages` (only 4 `user_account` actions) | `BindingResult` / `Model` attributes |
| 37 download actions writing to `response` | `void` controller methods taking `HttpServletResponse` (near-zero change) |
| Struts taglibs in JSPs | See §5 — the real work |

### Form bean hierarchy (all become plain POJOs)

```
PeptideProteinCommonForm  extends ActionForm      (fields: int[] projectSearchId, String ds, String queryJSON)
  ProteinCommonForm
    SearchViewProteinsForm
    MergedSearchViewProteinsForm
      DownloadMergedSearchViewProteinsForm
        DownloadProteinCLMSForm
  SearchViewPeptidesForm
  MergedSearchViewPeptidesForm
SingleRequestJSONStringFieldForm extends ActionForm  (field: String requestJSONString)
```

---

## 5. Where the real effort is: the JSPs

The Java side is mechanical. The **JSP taglib conversion is the bulk of the
manual work** (65 JSPs touched):

| Struts tag | Uses | Convert to |
|---|---|---|
| `<bean:write>` | 165 | `${...}` EL / `<c:out>` |
| `<html:form>` | 16 | Spring `<form:form>` or plain HTML |
| `<logic:iterate>` | 13 | `<c:forEach>` |
| `<html:messages>` / `<logic:messagesPresent>` | 8 | `<spring:message>` / model attrs |
| `<html:hidden>` | 4 | `<form:hidden>` / plain `<input>` |
| `<logic:forward>` | 1 | `<jsp:forward>` / redirect |

The dominant `<bean:write>` pattern is scriptable with find/replace; forms and
iterations need hand-fixing. One custom tag (`WEB-INF/taglibs/proxl.tld`, single
tag, used in 1 JSP) — keep as-is.

---

## 6. Recommended phased plan

1. **Add Spring 5.3 / Boot 2.7**; register `DispatcherServlet` for `*.do`
   alongside the still-running `ActionServlet` (split per-controller cutover).
2. **Convert leaf/simple actions first** — `forward`-only mappings and the 37
   download actions (no form binding).
3. **Convert form-backed page actions** (protein/peptide views); introduce the
   9 POJOs as `@ModelAttribute`.
4. **Convert JSPs taglib-by-taglib** (script `<bean:write>`, hand-fix the rest).
5. **Delete** Struts deps (`struts:struts`, `struts-el`), `struts-config.xml`,
   the 6 `.tld` files; remove `ActionServlet` from `web.xml`.
6. The **67 references** to `StrutsActionPathsConstants` / `StrutsGlobalForwardNames`
   are just string constants — rename, keep values (URLs unchanged).

---

## 7. Gotchas

- **Keep `*.do`.** Front-end JS and bookmarked URLs depend on `.do`; mapping
  `DispatcherServlet` to `*.do` means zero URL changes.
- **File upload.** `UploadFileForImportWebserviceAction` and
  `UploadPDBFileActionService` are Struts Actions acting as web services
  (return JSON, read raw input stream). Decide: convert to Spring
  `@PostMapping` with `MultipartFile`, or move under Jersey. Keeping them as
  Spring controllers preserves the `*.do` URLs. Verify Spring's
  `MultipartResolver` doesn't collide with their raw-stream reads.
- **Filter ordering.** `InitialServletFilter` sets request attributes the
  actions rely on; it runs on `/*`, so it still fires before `DispatcherServlet`.
  Confirm nothing depended on Struts-specific request attributes.
- **Dead repo.** `jcenter()` in `build.gradle` is shut down; will surface when
  adding Boot dependencies. Move everything to `mavenCentral()`.
- **Error pages.** `web.xml` maps 500/503 to `generalError.jsp`; preserve via
  `web.xml` or `@ExceptionHandler` / `ErrorController`.

---

## 8. Open decision

Packaging approach is the one decision to settle before starting:

- **WAR-on-Tomcat (recommended)** — truly preserves servlet spec + Jersey + the
  existing filters/servlets/listener registrations.
- **Spring Boot embedded** — more rework of servlet/filter registration; not
  required by the goal.
