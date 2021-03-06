<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html ng-app>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>C4.5 Classifier</title>
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0-beta.5/angular.min.js"></script>
        <script type="text/javascript" src="/c4.5/resources/js/IndexCtrl.js"></script>
        <link rel="stylesheet" type="text/css" href="/c4.5/resources/css/jquery.fullPage.css" />
        <link rel="stylesheet" type="text/css" href="/c4.5/resources/css/style.css" />

        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.1/jquery-ui.min.js"></script>	

        <script type="text/javascript" src="/c4.5/resources/js/jquery.fullPage.js"></script>


        <style>
            .normalNode
            {
                fill:black;
                stroke:black;
                stroke-width:2;
                z-index: 1000;

            }
            .selectedNode {
                fill:red;
                stroke:black;
                z-index: 1001;
            }
            .normalLine
            {
                stroke:black;
                stroke-width:2;
                z-index:1;

            }
            .selectedLine
            {
                stroke:red;
                stroke-width:2;
                z-index: 2;

            }

        </style>
    </head>
    <body ng-controller="IndexCtrl" bgcolor="#82B814">
        <div id="fullpage">
            <div class="dane-testowe">
                <c:forEach var="item" items="${trainingSet}">
                    <li>${item}</li>
                </c:forEach>
            <div class="pytania">
                <form name="queryForm"/>
                    Outlook:
                    <select onchange='showPath()' id="outlookSelect" ng-change="query()" ng-model="outlook" ng-init="outlook = 'sunny'">
                        <option value="sunny">Sunny</option>
                        <option value="overcast">Overcast</option>
                        <option value="rain">Rain</option>
                    </select>
                    <br/>
                    Temperature:
                    <select onchange='showPath()' id="temperatureSelect" ng-change="query()" ng-model="temperature" ng-init="temperature = 'hot'">
                        <option value="hot">Hot</option>
                        <option value="mild">Mild</option>
                        <option value="cool">Cool</option>
                    </select>
                    <br />
                    Humidity:
                    <select onchange='showPath()' id="humidityInput" ng-change="query()" ng-model="humidity" ng-init="humidity = 'high'">
                        <option value="high">High</option>
                        <option value="normal">Normal</option>
                    </select>
                    <br />
                    Windy:
                    <select onchange='showPath()' id="windyInput" ng-change="query()" ng-model="windy" ng-init="windy = 'true'">
                        <option value="true">True</option>
                        <option value="false">False</option>
                    </select>
                    <br />
                </form>
           </div>
           </div>
           <div class="drzewo" id="printGraph"></div>    
        </div>
        <script type="text/javascript">
                    var tree = ${treeJson};
                    var nodesList = ${nodesList};
                            function drawLine(x1, y1, x2, y2, label, node) {
                                var midX = getMidPoint(x1, y1, x2, y2)[0] + 5;
                                var midY = getMidPoint(x1, y1, x2, y2)[1];
                                var html = ""
                                html += "<line x1='" + x1 + "' y1='" + y1 + "' x2='" + x2 + "' y2='" + y2 + "' id='" + node.idString + "line' class='normalLine' />";
                                html += "<text x='" + (midX - 15) + "' y='" +
                                        (midY - 1) + "' fill='white' style='font-weight:bold'" +
                                        " transform='rotate(" + getAngleBetweenPoints(x1, y1, x2, y2) + " " + midX + "," + midY + ")'>" + label + "</text>";
                                html += "";
                                return html;
                            }
                    ;

                    function drawNode(x1, y1, node) {
                        var html = "";
                        html += "<ellipse cx=" + x1 + " cy=" + y1 + " rx='10' ry='10' id='" + node.idString + "' class='normalNode'/>"
                        if (node.classValue != null) {
                            html += "<text x='" + (x1 - 20) + "' y='" + (y1 + 30) + "' fill='black' style='font-size: 12px'>" + node.classValue + "</text>";
                        } else {
                            html += "<text x='" + (x1 + 20) + "' y='" + (y1 + 4) + "' fill='black' style='font-weight:bold'>" + node.attribute + "</text>";
                        }
                        return html;
                    }
                    ;

                    function drawGraph(width, height, tree) {
                        var html = "<svg width='" + width + "' height='" + height + "'>"
                        var x = width / 2;
                        var y = 15;
                        html += drawNode(x, y, tree.root);
                        html += drawChildren(tree.root, (x) - width * 0.2, (x) + width * 0.2, y + 150, x, y, 0.3);
                        html += "</svg>";
                        return html;
                    }
                    ;

                    function drawChildren(parent, minWidth, maxWidth, height, px, py, newMod) {
                        var html = "";
                        var x = minWidth;
                        var y = height;
                        var labelHeightMod = 0;
                        for (var i = 0; i < parent.children.length; i++) {
                            var child = parent.children[i];
                            html += drawNode(x, y, child);
                            html += drawLine(px, py, x, y, child.label, child);
                            labelHeightMod += 20;
                            var newMinWidth = x - ((maxWidth - minWidth) * newMod);
                            var newMaxWidth = x + ((maxWidth - minWidth) * newMod);
                            html += drawChildren(child, newMinWidth, newMaxWidth, height + 150, x, y, newMod * 0.8);
                            x += ((maxWidth - minWidth)) / (parent.children.length - 1);
                        }
                        return html;
                    }

                    function getMidPoint(x1, y1, x2, y2) {
                        var midX = ((x1 + x2) / 2);
                        var midY = ((y1 + y2) / 2);
                        return [midX, midY];
                    }

                    function getAngleBetweenPoints(x1, y1, x2, y2) {
                        var angleDeg = Math.atan2(y2 - y1, x2 - x1) * 180 / Math.PI;
                        return angleDeg;
                    }

                    function showPath(selection) {
                        var outlook = document.getElementById('outlookSelect').value;
                        var temperature = document.getElementById('temperatureSelect').value;
                        var humidity = document.getElementById('humidityInput').value;
                        var windy = document.getElementById('windyInput').value;
                        resetPath(tree.root);
                        paintPath(tree.root, outlook, temperature, humidity, windy);
                    }

                    function resetPath(node) {
                        for (var i = 0; i < node.children.length; i++) {
                            document.getElementById(node.children[i].idString)
                                    .setAttribute("class", "normalNode");
                            document.getElementById(node.children[i].idString + "line")
                                    .setAttribute("class", "normalLine");
                            resetPath(node.children[i]);
                        }
                    }

                    function paintPath(node, outlook, temperature, humidity, windy) {
                        if (node.label == 'root') {
                            document.getElementById(node.idString)
                                    .setAttribute("class", "selectedNode");
                        }
                        for (var i = 0; i < node.children.length; i++) {
                            if (node.attributeNum == 0) {
                                if (node.children[i].label == outlook) {
                                    document.getElementById(node.children[i].idString)
                                            .setAttribute("class", "selectedNode");
                                    document.getElementById(node.children[i].idString + "line")
                                            .setAttribute("class", "selectedLine");
                                    paintPath(node.children[i], outlook, temperature, humidity, windy);
                                } else {

                                }
                            } else if (node.attributeNum == 1) {
                                if (node.children[i].label == temperature) {
                                    document.getElementById(node.children[i].idString)
                                            .setAttribute("class", "selectedNode");
                                    document.getElementById(node.children[i].idString + "line")
                                            .setAttribute("class", "selectedLine");
                                    paintPath(node.children[i], outlook, temperature, humidity, windy);
                                } else {

                                }
                            } else if (node.attributeNum == 2) {
                                if (node.children[i].label == humidity) {
                                    document.getElementById(node.children[i].idString)
                                            .setAttribute("class", "selectedNode");
                                    document.getElementById(node.children[i].idString + "line")
                                            .setAttribute("class", "selectedLine");
                                    paintPath(node.children[i], outlook, temperature, humidity, windy);
                                } else {

                                }
                            } else if (node.attributeNum == 3) {
                                if (node.children[i].label == windy) {
                                    document.getElementById(node.children[i].idString)
                                            .setAttribute("class", "selectedNode");
                                    document.getElementById(node.children[i].idString + "line")
                                            .setAttribute("class", "selectedLine");
                                    paintPath(node.children[i], outlook, temperature, humidity, windy);
                                } else {

                                }
                            }
                        }
                    }
                    document.getElementById("printGraph").innerHTML = drawGraph(1000, 550, tree);
        </script>
    </body>
</html>
