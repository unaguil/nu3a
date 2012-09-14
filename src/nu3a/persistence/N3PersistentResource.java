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

package nu3a.persistence;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Esta interfaz especifica los m�todos que un objeto debe de cumplir para poder
 * guardarse en un medio persistente y cargarse de nuevo. Para ello, el objeto
 * generar� o interpretar� una descripci�n XML del mismo, seg�n el caso. Adem�s,
 * todo objeto persistente debe de implementar el siguiente m�todo est�tico para
 * poder cargarse desde un medio persistente:<br>
 * 
 * <i> public static N3PersistentResource loadInstance(Element infoNode,
 * NodeList nodes, N3PersistentResourceList resources, N3SceneReader reader,
 * N3Render render, N3Scene scene) throws N3InvalidCameraValuesException
 * 
 * Devuelve una instancia de la clase, a partir de los par�metros, y de la
 * descripci�n XML en infoNode.
 * 
 * Sus par�metros son: infoNode Descripci�n XML de la instacia a crear nodes
 * Rama XML con las descripciones de los recursos de la escena resources Lista
 * de recursos de la escena reader Instancia capaz de crear recursos que a�n no
 * se han creado render Render para el que se est� creando la escena scene
 * Escena que se est� creando
 * 
 * Devuelve una instancia de la clase con la informaci�n especificada </i>
 */
public interface N3PersistentResource extends N3PersistentResourceContainer {

	/**
	 * Obtiene la descripci�n XML del objeto en un Element DOM.
	 * 
	 * @param doc
	 *            Documento DOM a partir del que se generar� el elemento
	 * @param resources
	 *            Lista de recursos persistentes
	 * @return Descripci�n del elemento
	 */
	public Element getXMLDescription(Document doc,
			N3PersistentResourceList resources);
}
