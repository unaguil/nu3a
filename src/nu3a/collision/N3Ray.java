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

import nu3a.geometry.N3Point2D;
import nu3a.geometry.N3Point3D;
import nu3a.geometry.N3Polygon;
import nu3a.math.N3Vector3D;

/**
 * Esta clase define un rayo finito que puede ser utilizado para seleccionar
 * objetos mediante "picking". El rayo esta determinado mediante dos puntos, el
 * punto origen y el punto destino.
 */
public class N3Ray implements N3Collisionable {
	/**
	 * Punto origen del rayo.
	 */
	protected N3Point3D src;
	/**
	 * Punto destino del rayo.
	 */
	protected N3Point3D dest;

	// Vector del rayo
	private N3Vector3D d;

	/**
	 * Constructor de la clase. Permite obtener un rayo definido entre los
	 * puntos origen y destino indicados.
	 * 
	 * @param src
	 *            Punto origen del rayo.
	 * @param dest
	 *            Punto destino del rayo.
	 */
	public N3Ray(N3Point3D src, N3Point3D dest) {
		this.src = new N3Point3D(src.x, src.y, src.z);
		this.dest = new N3Point3D(dest.x, dest.y, dest.z);
		d = new N3Vector3D(dest.x - src.x, dest.y - src.y, dest.z - src.z);
		d.normalize();
	}

	private boolean testAABB(N3AABB aabb) {
		float t1, t2;
		float tNear = -100000000000.0f;
		float tFar = 100000000000.0f;
		// Comprobamos si el rayo es paralelo al eje X, si es asi y no esta
		// entre los planos de la caja no hay colisi�n.
		if (src.x == dest.x && !(src.x > aabb.minX && src.x < aabb.maxX))
			return false;
		else {
			t1 = (aabb.minX - src.x) / (dest.x - src.x);
			t2 = (aabb.maxX - src.x) / (dest.x - src.x);
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			if (t1 > tNear)
				tNear = t1;
			if (t2 < tFar)
				tFar = t2;
			if (tNear > tFar)
				return false;
			if (tFar < 0)
				return false;
		}

		if (src.y == dest.y && !(src.y > aabb.minY && src.y < aabb.maxY))
			return false;
		else {
			t1 = (aabb.minY - src.y) / (dest.y - src.y);
			t2 = (aabb.maxY - src.y) / (dest.y - src.y);
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			if (t1 > tNear)
				tNear = t1;
			if (t2 < tFar)
				tFar = t2;
			if (tNear > tFar)
				return false;
			if (tFar < 0)
				return false;
		}

		if (src.z == dest.z && !(src.z > aabb.minZ && src.z < aabb.maxZ))
			return false;
		else {
			t1 = (aabb.minZ - src.z) / (dest.z - src.z);
			t2 = (aabb.maxZ - src.z) / (dest.z - src.z);
			if (t1 > t2) {
				float temp = t1;
				t1 = t2;
				t2 = temp;
			}
			if (t1 > tNear)
				tNear = t1;
			if (t2 < tFar)
				tFar = t2;
			if (tNear > tFar)
				return false;
			if (tFar < 0)
				return false;
		}
		return true;
	}

	private boolean testPolygon(N3Polygon poly) {
		// Punto en el interior del poligono proyectado.
		N3Point2D inside;
		// Obtenemos el plano del poligono representado de la forma Ax + By +Cz
		// +D = 0
		N3Vector3D polyNormal = poly.getPolygonNormal();
		N3Point3D v = (N3Point3D) poly.getVertex(0);
		float polyD = -(polyNormal.x * v.x + polyNormal.y * v.y + polyNormal.z
				* v.z);
		// Primero comprobamos si hay intersecci�n entre el rayo y el plano
		// Substituimos (x,y,z)=(px,py,pz) + t(dx,dy,dz) en la ecuacion del
		// plano.
		// despejamos t = -(Apx + Bpy + Cpz + D)/(Adx + Bdy + Cdz)
		float s = (polyNormal.x * d.x + polyNormal.y * d.y + polyNormal.z * d.z);
		if (s == 0)
			return false;
		float t = -(polyNormal.x * src.x + polyNormal.y * src.y + polyNormal.z
				* src.z + polyD)
				/ s;
		if (t <= 0)
			return false;

		// Ahora hay que comprobar si el punto de corte del rayo con el plano,
		// esta dentro del poligono.
		// Obtenemos el punto de corte con el plano.
		N3Point3D p3D = new N3Point3D(src.x + t * d.x, src.y + t * d.y, src.z
				+ t * d.z);

		// Utilizamos el metodo de los half-spaces proyectando el pol�gono y el
		// punto ortograficamente en 2D,
		// seleccionando como plano de proyecci�n aquel en el que el pol�gono
		// tenga un mayor �rea.
		// El �rea es proporcional al las componentes A,B,C del vector normal
		// del pol�gono.
		N3Point2D p2D;
		// Indica en que plano hemos proyectado. 0 = yz, 1 = zx, 2 = xy.
		int plane;
		if (Math.abs(polyNormal.x) > Math.abs(polyNormal.y)
				&& Math.abs(polyNormal.x) > Math.abs(polyNormal.z)) {
			// Si es |A| proyectamos en el plano yz.
			p2D = new N3Point2D(p3D.y, p3D.z);
			plane = 0;
		} else if (Math.abs(polyNormal.y) > Math.abs(polyNormal.x)
				&& Math.abs(polyNormal.y) > Math.abs(polyNormal.z)) {
			// Si es |B| proyectamos en el plano zx.
			p2D = new N3Point2D(p3D.z, p3D.x);
			plane = 1;
		} else {
			// Si es |C| proyectamos en el plano xy.
			p2D = new N3Point2D(p3D.x, p3D.y);
			plane = 2;
		}

		// Para cada arista del poligono calculamos la ecuaci�n e(u,v)= au + bv
		// + c = 0 que representa un divisi�n del espacio en 2
		// partes, "half-space", el pol�gono queda definido por la intersecci�n
		// de todos ellos.
		// Para un poligono definido en sentido antihorario un punto esta dentro
		// de el si para todos las aristas
		// e < 0.
		for (int i = 0; i < poly.getSides(); i++) {
			// Proyectamos los puntos de la arista actual.
			N3Point3D v13D = (N3Point3D) poly.getVertex(i);
			N3Point3D v23D;
			N3Point2D v1, v2;
			if (i == poly.getSides() - 1)
				v23D = (N3Point3D) poly.getVertex(0);
			else
				v23D = (N3Point3D) poly.getVertex(i + 1);
			if (plane == 0) {
				v1 = new N3Point2D(v13D.y, v13D.z);
				v2 = new N3Point2D(v23D.y, v23D.z);
				inside = new N3Point2D(
						(((N3Point3D) poly.getVertex(0)).y
								+ ((N3Point3D) poly.getVertex(1)).y + ((N3Point3D) poly
								.getVertex(2)).y) / 3.0f,
						(((N3Point3D) poly.getVertex(0)).z
								+ ((N3Point3D) poly.getVertex(1)).z + ((N3Point3D) poly
								.getVertex(2)).z) / 3.0f);
			} else if (plane == 1) {
				v1 = new N3Point2D(v13D.z, v13D.x);
				v2 = new N3Point2D(v23D.z, v23D.x);
				inside = new N3Point2D(
						(((N3Point3D) poly.getVertex(0)).z
								+ ((N3Point3D) poly.getVertex(1)).z + ((N3Point3D) poly
								.getVertex(2)).z) / 3.0f,
						(((N3Point3D) poly.getVertex(0)).x
								+ ((N3Point3D) poly.getVertex(1)).x + ((N3Point3D) poly
								.getVertex(2)).x) / 3.0f);
			} else {
				v1 = new N3Point2D(v13D.x, v13D.y);
				v2 = new N3Point2D(v23D.x, v23D.y);
				inside = new N3Point2D(
						(((N3Point3D) poly.getVertex(0)).x
								+ ((N3Point3D) poly.getVertex(1)).x + ((N3Point3D) poly
								.getVertex(2)).x) / 3.0f,
						(((N3Point3D) poly.getVertex(0)).y
								+ ((N3Point3D) poly.getVertex(1)).y + ((N3Point3D) poly
								.getVertex(2)).y) / 3.0f);
			}

			// Obtenemos la representaci�n de la forma: au + bv + c = 0
			float a = (v1.y - v2.y);
			float b = (v2.x - v1.x);
			float c = (v1.x * v2.y - v2.x * v1.y);
			// Tenemos que comprobar si el poligono esta en sentido horario o
			// antihorario
			// obtenemos el producto vectorial entre el vector del rayo y la
			// normal del plano, si es negativo
			// esta dado la vuelta.
			float dir = d.dotProduct(polyNormal);
			if (dir > 0) {
				a = -a;
				b = -b;
				c = -c;
			}
			// Elegimos el signo de la comparaci�n
			if (a * inside.x + b * inside.y + c < 0) {
				if (a * p2D.x + b * p2D.y + c > 0)
					return false;
			} else {
				if (a * p2D.x + b * p2D.y + c < 0)
					return false;
			}
		}
		return true;
	}

	public boolean test(N3Collisionable c) {
		if (c instanceof N3AABB)
			return testAABB((N3AABB) c);
		if (c instanceof N3Polygon)
			return testPolygon((N3Polygon) c);
		return false;
	}

	public void setCollisionable(boolean c) {
	}

	public boolean getCollisionable() {
		return true;
	}

	public String toString() {
		String s = "";
		s += src + " --> " + dest;
		return s;
	}
}
