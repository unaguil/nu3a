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

package nu3a.render.software;

import nu3a.geometry.N3Point3D;
import nu3a.material.color.N3ColorRGBA;
import nu3a.math.N3Vector3D;

public class N3SoftwareLight {

	/**
	 * Componenten ambiental global
	 */
	protected static N3ColorRGBA ambientalLight;

	/**
	 * Emision del material sobre el que calcular la luz.
	 */
	protected static N3ColorRGBA matEmission;

	/**
	 * Color ambiental del material sobre el que calcular la luz.
	 */
	protected static N3ColorRGBA matAmbient;

	/**
	 * Color difuso del material sobre el que calcular la luz.
	 */
	protected static N3ColorRGBA matDiffuse;

	/**
	 * Color especular del material sobre el que calcular la luz.
	 */
	protected static N3ColorRGBA matSpecular;

	/**
	 * Componente ambiental de la luz.
	 */
	protected N3ColorRGBA ambiental;

	/**
	 * Componente difusa de la luz.
	 */
	protected N3ColorRGBA diffuse;

	/**
	 * Componente especular de la luz
	 */
	protected N3ColorRGBA specular;

	/**
	 * Exponente especular de la luz
	 */
	protected float shininess = 1.0f;

	/**
	 * Posici�n de la luz
	 */
	protected N3Point3D pos;

	/**
	 * Direcci�n de la luz.
	 */
	protected N3Vector3D dir;

	/**
	 * Indica si la luz esta o no activada
	 */
	protected boolean enable;

	/**
	 * Atenuaci�n constante de la luz.
	 */
	protected float Kc = 1.0f;

	/**
	 * Atenuaci�n linear de la luz.
	 */
	protected float Kl = 0.0f;

	/**
	 * Atenuacion cuadratica de la luz.
	 */
	protected float Kq = 0.0f;

	/**
	 * Efecto de spotlight
	 */
	protected float spotCutOff = 180.0f;

	protected float spotExp = 1.0f;

	protected N3ColorRGBA ambientTerm, diffuseTerm, specularTerm;

	protected N3Vector3D d;

	protected float cosine = (float) Math.cos(Math.toRadians(spotCutOff));

	/**
	 * Construye una luz con las componentes a color blanco, posicion (0,0,0) y
	 * direcci�n (0,0,-1)
	 */
	protected N3SoftwareLight() {
		if (ambientalLight == null) {
			ambientalLight = new N3ColorRGBA(0.2f, 0.2f, 0.2f);
			matEmission = new N3ColorRGBA(0, 0, 0);
			matAmbient = new N3ColorRGBA(0, 0, 0);
			matDiffuse = new N3ColorRGBA(1, 1, 1);
			matSpecular = new N3ColorRGBA(0, 0, 0);
		}
		ambiental = new N3ColorRGBA(0.2f, 0.2f, 0.2f);
		diffuse = new N3ColorRGBA(0.8f, 0.8f, 0.8f);
		specular = new N3ColorRGBA(0, 0, 0, 0);
		dir = new N3Vector3D();
		pos = new N3Point3D();
		enable = true;
		d = new N3Vector3D();
		ambientTerm = new N3ColorRGBA();
		diffuseTerm = new N3ColorRGBA();
		specularTerm = new N3ColorRGBA();
	}

	/**
	 * Permite activar o desactivar la luz.
	 * 
	 * @param s
	 *            Estado de la luz.
	 */
	public void setEnable(boolean s) {
		this.enable = s;
	}

	/**
	 * Permite saber si al luz esta o no activada.
	 * 
	 * @return Indica si la luz esta o no activada.
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * Permite especificar la luz ambiental de la escena.
	 * 
	 * @param c
	 *            Color de la luz ambiental de la escena.
	 */
	protected static void setAmbientalLight(N3ColorRGBA c) {
		ambientalLight.setData(c);
	}

	protected static void setMatAmbient(N3ColorRGBA c) {
		matAmbient.setData(c);
	}

	protected static void setMatDiffuse(N3ColorRGBA c) {
		matDiffuse.setData(c);
	}

	protected static void setMatSpecular(N3ColorRGBA c) {
		matSpecular.setData(c);
	}

	protected static void setMatEmission(N3ColorRGBA c) {
		matEmission.setData(c);
	}

	/**
	 * Permite especificar la componente ambiental de la luz.
	 * 
	 * @param c
	 *            Color de la componente ambiental de la luz.
	 */
	protected void setAmbiental(N3ColorRGBA c) {
		ambiental.setData(c);
	}

	/**
	 * Permite especificar la componente difusa de la luz.
	 * 
	 * @param c
	 *            Color de la componente difusa de la luz.
	 */
	protected void setDiffuse(N3ColorRGBA c) {
		diffuse.setData(c);
	}

	/**
	 * Permite especificar la componente especular de la luz.
	 * 
	 * @param c
	 *            Color de la componente especular de la luz.
	 */
	protected void setSpecular(N3ColorRGBA c) {
		specular.setData(c);
	}

	/**
	 * Permite especificar la direcci�n de la luz
	 * 
	 * @param d
	 *            Direcci�n de la luz.
	 */
	protected void setDirection(N3Vector3D d) {
		dir.x = d.x;
		dir.y = d.y;
		dir.z = d.z;
		dir.normalize();
	}

	/**
	 * Permite especificar la posici�n de la luz
	 * 
	 * @param p
	 *            Posici�n de la luz.
	 */
	protected void setPosition(N3Point3D p) {
		pos.x = p.x;
		pos.y = p.y;
		pos.z = p.z;
	}

	protected void setCutOff(float angle) {
		spotCutOff = angle;
		cosine = (float) Math.cos(Math.toRadians(spotCutOff));
	}

	protected void setSpotExp(float exp) {
		spotExp = exp;
	}

	protected void setConstantAtten(float Kc) {
		this.Kc = Kc;
	}

	protected void setLinearAtten(float Kl) {
		this.Kl = Kl;
	}

	protected void setQuadAtten(float Kq) {
		this.Kq = Kq;
	}

	// /Obtenido del OpenGL RedBook "The Mathematics of Lighting"
	/**
	 * Aplica la parte de la iluminaci�n independiente de la luz.
	 * 
	 * @param c
	 *            Color al que aplicar al iluminaci�n.
	 */
	public static void applyIndependantLight(N3ColorRGBA c) {
		c.R = matEmission.R + matAmbient.R * ambientalLight.R;
		c.G = matEmission.G + matAmbient.G * ambientalLight.G;
		c.B = matEmission.B + matAmbient.B * ambientalLight.B;
	}

	/**
	 * Aplica la iluminaci�n al color especificado segun la normal indicada.
	 * 
	 * @param v
	 *            Vertice sobre el que calcular
	 * @param c
	 *            Color al que aplicar la iluminaci�n
	 * @param n
	 *            Normal del punto al que se le aplica la iluminaci�n.
	 */
	protected void applyLight(N3Point3D v, N3ColorRGBA c, N3Vector3D n) {
		// Termino ambiental
		ambientTerm.R = matAmbient.R * ambiental.R;
		ambientTerm.G = matAmbient.G * ambiental.G;
		ambientTerm.B = matAmbient.B * ambiental.B;
		d.x = pos.x - v.x;
		d.y = pos.y - v.y;
		d.z = pos.z - v.z;
		float dist = d.length();
		// Termino diffuso
		d.normalize();
		float diff = d.dotProduct(n);
		if (diff < 0)
			diff = 0;
		diffuseTerm.R = diff * matDiffuse.R * diffuse.R;
		diffuseTerm.G = diff * matDiffuse.G * diffuse.G;
		diffuseTerm.B = diff * matDiffuse.B * diffuse.B;

		float atten = 1 / (Kc + Kl * dist + Kq * dist * dist);
		float spotEffect;
		if (spotCutOff == 180.0f)
			spotEffect = 1.0f;
		else {
			d.neg();
			spotEffect = d.dotProduct(dir);
			if (spotEffect < 0)
				spotEffect = 0;
			else if (spotEffect < cosine)
				spotEffect = 0;
		}

		float contrF = atten * spotEffect;
		// Termino especular
		if (diff != 0.0f) {
			float spec;
			d.z += 1.0f;
			spec = d.dotProduct(n);
			if (spec < 0)
				spec = 0.0f;
			spec *= shininess;
			specularTerm.R = spec * matSpecular.R * specular.R;
			specularTerm.G = spec * matSpecular.G * specular.G;
			specularTerm.B = spec * matSpecular.B * specular.B;
		} else
			specularTerm.R = specularTerm.G = specularTerm.B = 0.0f;

		c.R += contrF * (ambientTerm.R + diffuseTerm.R + specularTerm.R);
		c.G += contrF * (ambientTerm.G + diffuseTerm.G + specularTerm.G);
		c.B += contrF * (ambientTerm.B + diffuseTerm.B + specularTerm.B);
		// Recortar al rengo [0,1] los colores obtenidos
		if (c.R > 1.0f)
			c.R = 1.0f;
		if (c.G > 1.0f)
			c.G = 1.0f;
		if (c.B > 1.0f)
			c.B = 1.0f;
	}
}
