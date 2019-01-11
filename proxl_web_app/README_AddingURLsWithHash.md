# README #

Development for Proxl Web app:

HTML anchors with format 

	<a href="#anchorlabel">text</a>

will probably not as expected since every page in this web app 
has in the <head> section a <base> tag with href="/appcontext/" which with default context is href="/proxl/"

Info on <base> tag at https://developer.mozilla.org/en-US/docs/Web/HTML/Element/base:

The usage of an anchor tag within the page, e.g. <a href="#anchor">anchor</a> is resolved by using the base url as reference and triggers an http request to the base url.

Example:

The base url:
<base href="http://www.example.com/">

The anchor:
<a href="#anchor">Anker</a>

Refers to:
http://www.example.com/#anchor
