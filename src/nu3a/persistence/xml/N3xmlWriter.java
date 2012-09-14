/*
 *	Copyright (c) 2003 Jorge García, Unai Aguilera
 *
 *	This file is part of Nu3A.
 *
 *   Nu3A is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.

 *   Nu3A is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Nu3A.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *	Authors: Jorge García <bardok@gmail.com>, Unai Aguilera <gkalgan@gmail.com>
 */

package nu3a.persistence.xml;

import java.io.OutputStream;
import java.io.PrintStream;

import nu3a.persistence.N3SceneWriter;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementa un N3SceneWriter de manera que se escribe el documento DOM en
 * documento XML.
 */
public class N3xmlWriter extends N3SceneWriter {

	private PrintStream ps;

	private String indentString = "";

	private void writeNode(Element node, PrintStream ps) {
		ps.print(indentString + "<" + node.getTagName());
		NamedNodeMap attribs = node.getAttributes();
		Node n;
		NodeList nl;
		if (attribs != null)
			for (int i = 0; i < attribs.getLength(); i++) {
				n = attribs.item(i);
				ps.print(" " + n.getNodeName() + "=\"" + n.getNodeValue()
						+ "\"");
			}
		if (!node.hasChildNodes())
			ps.println(" />");
		else {
			String prevIndent = indentString;
			ps.println(">");
			indentString = "\t" + indentString;
			nl = node.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i) instanceof Element)
					writeNode((Element) nl.item(i), ps);
			}
			indentString = prevIndent;
			ps.println(indentString + "</" + node.getTagName() + ">");

		}
	}

	private void resetIndentation() {
		indentString = "";
	}

	protected void saveToInternalFormat(OutputStream os) {
		ps = new PrintStream(os);
		resetIndentation();
		ps.println("<?xml version=\"1.0\"?>");
		writeNode(document.getDocumentElement(), ps);
		ps.flush();
	}
}
