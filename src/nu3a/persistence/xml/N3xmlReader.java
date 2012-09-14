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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import nu3a.persistence.N3SceneReader;

import org.w3c.dom.Document;

/**
 * Implementa un N3SceneReader de manera que obtiene el documento DOM a partir
 * de un documento XML.
 */
public class N3xmlReader extends N3SceneReader {

	protected Document readFromInternalFormat(InputStream is) {
		try {
			Document d = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().parse(is);
			return d;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		;
		return null;
	}
}
