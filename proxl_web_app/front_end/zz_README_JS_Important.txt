



Many of the Javascript files under "front_end"
contain variables and functions attached to "window.".

This is done since the HTML (JSP and Handlebars templates and strings in Javascript)
have many places with onclick and href="javascript:XX"
that references the variables and functions defined in the Javascript files.

Some non-root JS files have their own '$(document).ready(function()  { '

