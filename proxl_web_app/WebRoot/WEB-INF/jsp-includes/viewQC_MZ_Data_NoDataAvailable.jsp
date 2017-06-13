<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  viewQC_MZ_Data_NoDataAvailable.jsp  --%>

<%--  Expects page variable 'noDataLinkType' --%>

<c:set var="chartTitle">PSM Count vs/ m/z (<c:out value="${ noDataLinkType }"></c:out>)</c:set>


<td style="padding: 4px;">
 <div class=" chart-standard-container-div chart_outer_container_for_download_jq " style="opacity: .5"> 
  <div style="position: relative; text-align: center;" >
	  <div style="font-size: 20px; font-weight: bold; width: 200px; position: absolute; left: 150px; top: 120px; background-color: white;">
		 	No Data Available
	  </div>	 
  </div>
  <div class="chart_container_jq chart_container_for_download_jq">
  
<svg width="500" height="300" aria-label="A chart." style="overflow: hidden;">
<defs id="defs">
<clipPath id="_ABSTRACT_RENDERER_ID_3">
<rect x="94" y="58" width="313" height="185">
</rect>
</clipPath>
</defs>
<rect x="0" y="0" width="500" height="300" stroke="none" stroke-width="0" fill="#ffffff">
</rect>
<g>
<text text-anchor="start" x="94" y="36.75" font-family="Arial" font-size="15" font-weight="bold" stroke="none" stroke-width="0" fill="#000000"
><c:out value="${ chartTitle }"></c:out>
</text>
<rect x="94" y="24" width="313" height="15" stroke="none" stroke-width="0" fill-opacity="0" fill="#ffffff">
</rect>
</g>
<g>
<rect x="94" y="58" width="313" height="185" stroke="none" stroke-width="0" fill-opacity="0" fill="#ffffff">
</rect>
<%-- 
<g clip-path="url(http://192.168.1.88:8080/proxl/qc.do?projectSearchId=185#_ABSTRACT_RENDERER_ID_3)">
--%>
<%--  Horizontal lines at tick marks --%>
<%-- 
<g>
<rect x="94" y="242" width="313" height="1" stroke="none" stroke-width="0" fill="#cccccc">
</rect>
<rect x="94" y="196" width="313" height="1" stroke="none" stroke-width="0" fill="#cccccc">
</rect>
<rect x="94" y="150" width="313" height="1" stroke="none" stroke-width="0" fill="#cccccc">
</rect>
<rect x="94" y="104" width="313" height="1" stroke="none" stroke-width="0" fill="#cccccc">
</rect>
<rect x="94" y="58" width="313" height="1" stroke="none" stroke-width="0" fill="#cccccc">
</rect>
</g>
--%>
<%-- 
			<rect>  for actual data bars.  Comment out to hide them
<g>
<rect x="94.5" y="206" width="12" height="36" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="106.5" y="220" width="11" height="22" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="117.5" y="209" width="12" height="33" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="129.5" y="192" width="11" height="50" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="140.5" y="169" width="12" height="73" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="152.5" y="96" width="11" height="146" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="163.5" y="160" width="12" height="82" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="175.5" y="110" width="11" height="132" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="186.5" y="135" width="12" height="107" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="198.5" y="105" width="12" height="137" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="210.5" y="130" width="11" height="112" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="221.5" y="105" width="12" height="137" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="233.5" y="107" width="11" height="135" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="244.5" y="195" width="12" height="47" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="256.5" y="160" width="11" height="82" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="267.5" y="110" width="12" height="132" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="279.5" y="204" width="11" height="38" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="290.5" y="176" width="12" height="66" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="302.5" y="192" width="12" height="50" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="314.5" y="222" width="11" height="20" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="325.5" y="222" width="12" height="20" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="337.5" y="202" width="11" height="40" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="348.5" y="234" width="12" height="8" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="360.5" y="222" width="11" height="20" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="371.5" y="232" width="12" height="10" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="383.5" y="236" width="11" height="6" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
<rect x="394.5" y="236" width="12" height="6" stroke="#a55353" stroke-width="1" fill="#a55353">
</rect>
</g>
--%>
<g>
<rect x="94" y="242" width="313" height="1" stroke="none" stroke-width="0" fill="#333333">
</rect>
</g>
<%-- hide closing <g> for clipping --%>
<%-- 
</g>
--%>
<g>
</g>
<g>
<g>
<text text-anchor="middle" x="143.52866158939014" y="260.2" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">600
</text>
</g>
<g>
<text text-anchor="middle" x="200.34392214287692" y="260.2" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">750
</text>
</g>
<g>
<text text-anchor="middle" x="257.1591826963637" y="260.2" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">900
</text>
</g>
<g>
<text text-anchor="middle" x="313.9744432498505" y="260.2" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">1,050
</text>
</g>
<g>
<text text-anchor="middle" x="370.78970380333726" y="260.2" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">1,200
</text>
</g>
<g>
<text text-anchor="end" x="82" y="246.7" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">0
</text>
</g>
<g>
<text text-anchor="end" x="82" y="200.7" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">20
</text>
</g>
<g>
<text text-anchor="end" x="82" y="154.7" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">40
</text>
</g>
<g>
<text text-anchor="end" x="82" y="108.7" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">60
</text>
</g>
<g>
<text text-anchor="end" x="82" y="62.7" font-family="Arial" font-size="12" stroke="none" stroke-width="0" fill="#444444">80
</text>
</g>
</g>
</g>
<g>
<g>
<text text-anchor="middle" x="250.5" y="285.9" font-family="Arial" font-size="14" font-style="italic" stroke="none" stroke-width="0" fill="#000000">M/Z
</text>
<rect x="94" y="274" width="313" height="14" stroke="none" stroke-width="0" fill-opacity="0" fill="#ffffff">
</rect>
</g>
<g>
<text text-anchor="middle" x="39.4" y="150.5" font-family="Arial" font-size="14" font-style="italic" transform="rotate(-90 39.4 150.5)" stroke="none" stroke-width="0" fill="#000000">Count
</text>
<path d="M27.499999999999993,243L27.500000000000004,58L41.50000000000001,58L41.49999999999999,243Z" stroke="none" stroke-width="0" fill-opacity="0" fill="#ffffff">
</path>
</g>
</g>
<g>
</g>
</svg>


					
	  </div>
	 </div>

	</td>