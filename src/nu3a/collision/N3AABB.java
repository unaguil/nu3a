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

package nu3a.collision;

import nu3a.geometry.N3GeometryData;
import nu3a.geometry.N3Point3D;
import nu3a.geometry.N3Polygon;
import nu3a.geometry.N3VertexData;
import nu3a.material.color.N3ColorData;
import nu3a.material.color.N3ColorRGBA;
import nu3a.math.N3Matrix4D;
import nu3a.render.N3Render;

/**
 * Esta clase define una Axis Aligned Bounding Box que puede ser utilizada en la
 * detecci�n de colisiones. Las cajas AABB se caracter�zan porque sus caras son
 * paralelas a los ejes de coordenadas del mundo de la escena. No se ajustan
 * totalmente a la geometr�a que contienen, pero permiten realizar el test de
 * colisi�n de una forma m�s r�pida.
 */
public class N3AABB extends N3BoundingVolume {
	/**
	 * Valor m�ximo en x de la caja.
	 */
	protected float maxX;
	/**
	 * Valor m�nimo en x de la caja.
	 */
	protected float minX;
	/**
	 * Valor m�ximo en y de la caja.
	 */
	protected float maxY;
	/**
	 * Valor m�nimo en y de la caja.
	 */
	protected float minY;
	/**
	 * Valor m�ximo en z de la caja.
	 */
	protected float maxZ;
	/**
	 * Valor m�nimo en z de la caja.
	 */
	protected float minZ;

	private N3Point3D v1, v2, v3, v4;
	private N3Point3D v5, v6, v7, v8;

	private N3GeometryData geometryData;

	boolean firstAdd;

	/**
	 * Construye una caja AABB de tama�o 0.
	 */
	public N3AABB() {
		maxX = minX = 0;
		maxY = minY = 0;
		maxZ = minZ = 0;
		v1 = new N3Point3D();
		v2 = new N3Point3D();
		v3 = new N3Point3D();
		v4 = new N3Point3D();
		v5 = new N3Point3D();
		v6 = new N3Point3D();
		v7 = new N3Point3D();
		v8 = new N3Point3D();
		firstAdd = true;
	}

	/**
	 * Construye una caja AABB a partir de la geometr�a indicada.
	 * 
	 * @param geometryData
	 *            Geometr�a a partir de la cual construir el vol�men de
	 *            colisi�n.
	 */
	public N3AABB(N3GeometryData geometryData) {
		this.geometryData = geometryData;

		v1 = new N3Point3D();
		v2 = new N3Point3D();
		v3 = new N3Point3D();
		v4 = new N3Point3D();
		v5 = new N3Point3D();
		v6 = new N3Point3D();
		v7 = new N3Point3D();
		v8 = new N3Point3D();
	}

	/**
	 * Permite duplicar la caja AABB indicada en la caja actual.
	 * 
	 * @param bv
	 *            Caja AABB de la que obtener los datos a duplicar.
	 */
	public void setData(N3BoundingVolume bv) {
		if (bv instanceof N3AABB) {
			N3AABB aabb = (N3AABB) bv;
			maxX = aabb.maxX;
			minX = aabb.minX;
			maxY = aabb.maxY;
			minY = aabb.minY;
			maxZ = aabb.maxZ;
			minZ = aabb.minZ;
		}
	}

	/**
	 * Calcula la caja AABB transformando previamente la geometr�a del objeto
	 * con la matriz indicada.
	 */
	public void calcule(N3Matrix4D m) {
		maxX = minX = 0;
		maxY = minY = 0;
		maxZ = minZ = 0;
		boolean first = true;

		// Calculamos la caja AABB a partir de la geometria

		for (int i = 0; i < geometryData.polygonCount(); i++) {
			N3Polygon poly = geometryData.getPolygon(i);
			for (int j = 0; j < poly.getSides(); j++) {
				N3Point3D vertex = (N3Point3D) poly.getVertex(j);
				N3Point3D v = new N3Point3D(vertex.x, vertex.y, vertex.z);
				v = m.mult(v);
				if (first) {
					maxX = minX = v.x;
					maxY = minY = v.y;
					maxZ = minZ = v.z;
					first = false;
				} else {
					if (v.x < minX)
						minX = v.x;
					if (v.x > maxX)
						maxX = v.x;

					if (v.y < minY)
						minY = v.y;
					if (v.y > maxY)
						maxY = v.y;

					if (v.z < minZ)
						minZ = v.z;
					if (v.z > maxZ)
						maxZ = v.z;
				}
			}
		}
	}

	/**
	 * Permite a�adir a la caja AABB actual otro volumen de colisi�n.
	 * Actualmente, solo esta implementado la suma con otras cajas AABB.
	 * 
	 * @param bv
	 *            Vol�men de colisi�n a a�adir a la caja AABB.
	 */
	public void add(N3BoundingVolume bv) {
		if (bv instanceof N3AABB) {
			N3AABB aabb = (N3AABB) bv;
			if (firstAdd) {
				minX = aabb.minX;
				maxX = aabb.maxX;
				minY = aabb.minY;
				maxY = aabb.maxY;
				minZ = aabb.minZ;
				maxZ = aabb.maxZ;
				firstAdd = false;
			} else if (aabb.minX < minX)
				minX = aabb.minX;
			if (aabb.maxX > maxX)
				maxX = aabb.maxX;

			if (aabb.minY < minY)
				minY = aabb.minY;
			if (aabb.maxY > maxY)
				maxY = aabb.maxY;

			if (aabb.minZ < minZ)
				minZ = aabb.minZ;
			if (aabb.maxZ > maxZ)
				maxZ = aabb.maxZ;
		}
	}

	/**
	 * Realiza el test de la caja AABB con el volumen de colisi�n indicado.
	 * Actualmente solo esta implementado el test con otras AABB.
	 * 
	 * @param bv
	 *            Vol�men de conlisi�n con el que realizar el test.
	 */
	public boolean test(N3BoundingVolume bv) {
		if (bv instanceof N3AABB) {
			N3AABB aabb = (N3AABB) bv;
			return (maxX > aabb.minX && minX < aabb.maxX && maxY > aabb.minY
					&& minY < aabb.maxY && maxZ > aabb.minZ && minZ < aabb.maxZ);
		}
		return false;
	}

	/**
	 * Dibuja la caja AABB.
	 * 
	 * @param render
	 *            Render en el que realizar el dibujado de la caja.
	 */
	public void draw(N3Render render) {
		N3VertexData v = new N3VertexData();
		N3ColorData c = new N3ColorData();
		c.addColor(new N3ColorRGBA(1.0f, 1.0f, 1.0f));

		v1.x = minX;
		v1.y = maxY;
		v1.z = maxZ;

		v2.x = minX;
		v2.y = minY;
		v2.z = maxZ;

		v3.x = maxX;
		v3.y = minY;
		v3.z = maxZ;

		v4.x = maxX;
		v4.y = maxY;
		v4.z = maxZ;

		v5.x = minX;
		v5.y = maxY;
		v5.z = minZ;

		v6.x = minX;
		v6.y = minY;
		v6.z = minZ;

		v7.x = maxX;
		v7.y = minY;
		v7.z = minZ;

		v8.x = maxX;
		v8.y = maxY;
		v8.z = minZ;

		v.addVertex(v1);
		v.addVertex(v2);
		v.addVertex(v2);
		v.addVertex(v3);
		v.addVertex(v3);
		v.addVertex(v4);
		v.addVertex(v4);
		v.addVertex(v1);

		v.addVertex(v5);
		v.addVertex(v6);
		v.addVertex(v6);
		v.addVertex(v7);
		v.addVertex(v7);
		v.addVertex(v8);
		v.addVertex(v8);
		v.addVertex(v5);

		v.addVertex(v1);
		v.addVertex(v5);
		v.addVertex(v2);
		v.addVertex(v6);

		v.addVertex(v4);
		v.addVertex(v8);
		v.addVertex(v3);
		v.addVertex(v7);

		render.drawData(v, render.N3_LINES_DATA, c);
	}

	/**
	 * Obtiene la representaci�n en forma de string de la caja AABB.
	 * 
	 * @return Representaci�n en forma de string de la caja AABB.
	 */
	public String toString() {
		String res = "";
		res += "Min: " + minX + " " + minY + " " + minZ + "\n";
		res += "Max: " + maxX + " " + maxY + " " + maxZ + "\n";
		return res;
	}
}
