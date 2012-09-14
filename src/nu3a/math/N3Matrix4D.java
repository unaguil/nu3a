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

package nu3a.math;

import nu3a.geometry.N3Point3D;

/**
 * Representa matrices homogeneas de 4 dimensiones.
 */
public class N3Matrix4D {

	/**
	 * La matriz se almacena de forma lineal.
	 */
	protected float[] matrix;

	/**
	 * Construye una matriz identidad.
	 */
	public N3Matrix4D() {
		matrix = new float[16];
		identity();
	}

	/**
	 * Construye una matriz identidad, como una copia de la matriz indicada.
	 * 
	 * @param m
	 *            Matriz fuente
	 */
	public N3Matrix4D(N3Matrix4D m) {
		matrix = new float[16];
		System.arraycopy(m.matrix, 0, matrix, 0, 16);
	}

	/**
	 * Copia la matriz por referencia en el objeto.
	 * 
	 * @param m
	 *            Matriz fuente
	 */
	public void setData(N3Matrix4D m) {
		System.arraycopy(m.matrix, 0, matrix, 0, 16);
	}

	/**
	 * Obtiene la matriz contenida en el objeto.
	 * 
	 * @return Elementos de la matriz
	 */
	public float[] getMatrix() {
		return matrix;
	}

	/**
	 * Pone a cero cada elemento de la matriz.
	 */
	public void zero() {
		for (int i = 16; i-- > 0;)
			matrix[i] = 0;
	}

	/**
	 * Convierte la matriz en la matriz unidad.
	 */
	public void identity() {
		zero();
		matrix[0] = 1.0f;
		matrix[5] = 1.0f;
		matrix[10] = 1.0f;
		matrix[15] = 1.0f;
	}

	/**
	 * Suma a la matriz actual la matriz que se le indique como parametro.
	 * 
	 * @param m
	 *            Matriz a sumar
	 */
	public void add(N3Matrix4D m) {
		matrix[0] += m.matrix[0];
		matrix[1] += m.matrix[1];
		matrix[2] += m.matrix[2];
		matrix[3] += m.matrix[3];
		matrix[4] += m.matrix[4];
		matrix[5] += m.matrix[5];
		matrix[6] += m.matrix[6];
		matrix[7] += m.matrix[7];
		matrix[8] += m.matrix[0];
		matrix[9] += m.matrix[9];
		matrix[10] += m.matrix[10];
		matrix[11] += m.matrix[11];
		matrix[12] += m.matrix[12];
		matrix[13] += m.matrix[13];
		matrix[14] += m.matrix[14];
		matrix[15] += m.matrix[15];
	}

	/**
	 * Multiplica la matriz por el valor escalar especificado.
	 * 
	 * @param s
	 *            Factor por el que escalar
	 */
	public void mult(float s) {
		matrix[0] *= s;
		matrix[1] *= s;
		matrix[2] *= s;
		matrix[3] *= s;
		matrix[4] *= s;
		matrix[5] *= s;
		matrix[6] *= s;
		matrix[7] *= s;
		matrix[8] *= s;
		matrix[9] *= s;
		matrix[10] *= s;
		matrix[11] *= s;
		matrix[12] *= s;
		matrix[13] *= s;
		matrix[14] *= s;
		matrix[15] *= s;
	}

	/*
	 * Multiplica la matriz por la matriz indicada como parametro.
	 * 
	 * @param m Matriz por la que multiplicar
	 */
	public void mult(N3Matrix4D m) {
		float[] res = new float[16];
		res[0] = (matrix[0] * m.matrix[0]) + (matrix[4] * m.matrix[1])
				+ (matrix[8] * m.matrix[2]) + (matrix[12] * m.matrix[3]);
		res[4] = (matrix[0] * m.matrix[4]) + (matrix[4] * m.matrix[5])
				+ (matrix[8] * m.matrix[6]) + (matrix[12] * m.matrix[7]);
		res[8] = (matrix[0] * m.matrix[8]) + (matrix[4] * m.matrix[9])
				+ (matrix[8] * m.matrix[10]) + (matrix[12] * m.matrix[11]);
		res[12] = (matrix[0] * m.matrix[12]) + (matrix[4] * m.matrix[13])
				+ (matrix[8] * m.matrix[14]) + (matrix[12] * m.matrix[15]);

		res[1] = (matrix[1] * m.matrix[0]) + (matrix[5] * m.matrix[1])
				+ (matrix[9] * m.matrix[2]) + (matrix[13] * m.matrix[3]);
		res[5] = (matrix[1] * m.matrix[4]) + (matrix[5] * m.matrix[5])
				+ (matrix[9] * m.matrix[6]) + (matrix[13] * m.matrix[7]);
		res[9] = (matrix[1] * m.matrix[8]) + (matrix[5] * m.matrix[9])
				+ (matrix[9] * m.matrix[10]) + (matrix[13] * m.matrix[11]);
		res[13] = (matrix[1] * m.matrix[12]) + (matrix[5] * m.matrix[13])
				+ (matrix[9] * m.matrix[14]) + (matrix[13] * m.matrix[15]);

		res[2] = (matrix[2] * m.matrix[0]) + (matrix[6] * m.matrix[1])
				+ (matrix[10] * m.matrix[2]) + (matrix[14] * m.matrix[3]);
		res[6] = (matrix[2] * m.matrix[4]) + (matrix[6] * m.matrix[5])
				+ (matrix[10] * m.matrix[6]) + (matrix[14] * m.matrix[7]);
		res[10] = (matrix[2] * m.matrix[8]) + (matrix[6] * m.matrix[9])
				+ (matrix[10] * m.matrix[10]) + (matrix[14] * m.matrix[11]);
		res[14] = (matrix[2] * m.matrix[12]) + (matrix[6] * m.matrix[13])
				+ (matrix[10] * m.matrix[14]) + (matrix[14] * m.matrix[15]);

		res[3] = (matrix[3] * m.matrix[0]) + (matrix[7] * m.matrix[1])
				+ (matrix[11] * m.matrix[2]) + (matrix[15] * m.matrix[3]);
		res[7] = (matrix[3] * m.matrix[4]) + (matrix[7] * m.matrix[5])
				+ (matrix[11] * m.matrix[6]) + (matrix[15] * m.matrix[7]);
		res[11] = (matrix[3] * m.matrix[8]) + (matrix[7] * m.matrix[9])
				+ (matrix[11] * m.matrix[10]) + (matrix[15] * m.matrix[11]);
		res[15] = (matrix[3] * m.matrix[12]) + (matrix[7] * m.matrix[13])
				+ (matrix[11] * m.matrix[14]) + (matrix[15] * m.matrix[15]);
		System.arraycopy(res, 0, this.matrix, 0, 16);
	}

	/**
	 * Transpone la matriz.
	 */
	public void transpose() {
		float[] res = new float[16];
		res[0] = matrix[0];
		res[1] = matrix[4];
		res[2] = matrix[8];
		res[3] = matrix[12];
		res[4] = matrix[1];
		res[5] = matrix[5];
		res[6] = matrix[9];
		res[7] = matrix[13];
		res[8] = matrix[2];
		res[9] = matrix[6];
		res[10] = matrix[10];
		res[11] = matrix[14];
		res[12] = matrix[3];
		res[13] = matrix[7];
		res[14] = matrix[11];
		res[15] = matrix[15];
		System.arraycopy(res, 0, this.matrix, 0, 16);
	}

	/**
	 * Invierte la matriz.
	 */
	public void inverse() {
		float[] res = new float[16];
		float det;

		det = matrix[0] * (matrix[5] * matrix[10] - matrix[6] * matrix[9])
				- matrix[4] * (matrix[1] * matrix[10] - matrix[2] * matrix[9])
				+ matrix[8] * (matrix[1] * matrix[6] - matrix[2] * matrix[5]);

		res[0] = (matrix[5] * matrix[10] - matrix[6] * matrix[9]);
		res[4] = -(matrix[4] * matrix[10] - matrix[6] * matrix[8]);
		res[8] = (matrix[4] * matrix[9] - matrix[5] * matrix[8]);
		res[12] = 0.0f;
		res[1] = -(matrix[1] * matrix[10] - matrix[2] * matrix[9]);
		res[5] = (matrix[0] * matrix[10] - matrix[2] * matrix[8]);
		res[9] = -(matrix[0] * matrix[9] - matrix[1] * matrix[8]);
		res[12] = 0.0f;
		res[2] = (matrix[1] * matrix[6] - matrix[2] * matrix[5]);
		res[6] = -(matrix[0] * matrix[6] - matrix[2] * matrix[4]);
		res[10] = (matrix[0] * matrix[5] - matrix[1] * matrix[4]);
		res[12] = 0.0f;
		res[3] = 0.0f;
		res[7] = 0.0f;
		res[11] = 0.0f;
		res[15] = 1.0f;

		res[12] = -((res[0] * matrix[12]) + (res[4] * matrix[13]) + (res[8] * matrix[14]));
		res[13] = -((res[1] * matrix[12]) + (res[5] * matrix[13]) + (res[9] * matrix[14]));
		res[14] = -((res[2] * matrix[12]) + (res[6] * matrix[13]) + (res[10] * matrix[14]));
		res[15] = 1.0f;
		System.arraycopy(res, 0, matrix, 0, 16);
	}

	/**
	 * Multiplica la matriz por el vector especificado.
	 * 
	 * @param v
	 *            Vector por el que multiplicar
	 * @return Vector resultado
	 */
	public N3Vector3D mult(N3Vector3D v) {
		N3Vector3D res = new N3Vector3D();
		res.x = matrix[0] * v.x + matrix[4] * v.y + matrix[8] * v.z;
		res.y = matrix[1] * v.x + matrix[5] * v.y + matrix[9] * v.z;
		res.z = matrix[2] * v.x + matrix[6] * v.y + matrix[10] * v.z;
		return res;
	}

	/**
	 * Convierte la matriz actual en una matriz que representa la translacion
	 * especificada por el vector.
	 */
	public void translate(N3Vector3D v) {
		identity();
		matrix[12] = v.x;
		matrix[13] = v.y;
		matrix[14] = v.z;
	}

	/**
	 * Convierte la matriz actual en una matriz que representa el escalado
	 * especificado por el vector.
	 * 
	 * @param v
	 *            Vector que representa el escalado
	 */
	public void scale(N3Vector3D v) {
		identity();
		matrix[0] = v.x;
		matrix[5] = v.y;
		matrix[10] = v.z;
	}

	/**
	 * Convierte la matriz actual en una matriz que representa la rotacion en el
	 * eje X determinada por el angulo especificado.
	 * 
	 * @param angle
	 *            Angulo de rotacion
	 */
	public void rotateX(float angle) {
		identity();
		float s = (float) Math.sin(Math.toRadians(angle));
		float c = (float) Math.cos(Math.toRadians(angle));
		matrix[5] = c;
		matrix[6] = s;
		matrix[9] = -s;
		matrix[10] = c;
	}

	/**
	 * Convierte la matriz actual en una matriz que representa la rotacion en el
	 * eje Y determinada por el angulo especificado.
	 * 
	 * @param angle
	 *            Angulo de rotacion
	 */
	public void rotateY(float angle) {
		identity();
		float s = (float) Math.sin(Math.toRadians(angle));
		float c = (float) Math.cos(Math.toRadians(angle));
		matrix[0] = c;
		matrix[2] = -s;
		matrix[8] = s;
		matrix[10] = c;
	}

	/**
	 * Convierte la matriz actual en una matriz que representa la rotacion en el
	 * eje Z determinada por el angulo especificado.
	 * 
	 * @param angle
	 *            Angulo de rotacion
	 */
	public void rotateZ(float angle) {
		identity();
		float s = (float) Math.sin(Math.toRadians(angle));
		float c = (float) Math.cos(Math.toRadians(angle));
		matrix[0] = c;
		matrix[1] = s;
		matrix[4] = -s;
		matrix[5] = c;
	}

	/**
	 * Convierte la matriz actual en una matriz que representa la rotacion
	 * alrededor de un eje arbitrario especificado por el vector y un angulo
	 * determinado.
	 * 
	 * @param angle
	 *            Angulo de rotacion
	 * @param v
	 *            Vector sobre el que rotar
	 */
	public void rotate(float angle, N3Vector3D v) {
		N3Vector3D rNormalized = N3Vector3D.normalize(v);
		float s = (float) Math.sin(Math.toRadians(angle));
		float c = (float) Math.cos(Math.toRadians(angle));

		matrix[0] = c + (1 - c) * rNormalized.x * rNormalized.x;
		matrix[1] = (1 - c) * rNormalized.x * rNormalized.y + rNormalized.z * s;
		matrix[2] = (1 - c) * rNormalized.x * rNormalized.z - rNormalized.y * s;
		matrix[3] = 0;
		matrix[4] = (1 - c) * rNormalized.x * rNormalized.y - rNormalized.z * s;
		matrix[5] = c + (1 - c) * rNormalized.y * rNormalized.y;
		matrix[6] = (1 - c) * rNormalized.y * rNormalized.z + rNormalized.x * s;
		matrix[7] = 0;
		matrix[8] = (1 - c) * rNormalized.x * rNormalized.z + rNormalized.y * s;
		matrix[9] = (1 - c) * rNormalized.y * rNormalized.z - rNormalized.x * s;
		matrix[10] = c + (1 - c) * rNormalized.z * rNormalized.z;
		matrix[11] = 0;
		matrix[12] = matrix[13] = matrix[14] = 0;
		matrix[15] = 1.0f;
	}

	/**
	 * Multiplica el punto por la matriz.
	 * 
	 * @param p
	 *            Punto que transformar por la matriz.
	 * @return El punto transformado por la matriz.
	 */
	public N3Point3D mult(N3Point3D p) {
		N3Point3D res = new N3Point3D();
		res.x = matrix[0] * p.x + matrix[4] * p.y + matrix[8] * p.z
				+ matrix[12];
		res.y = matrix[1] * p.x + matrix[5] * p.y + matrix[9] * p.z
				+ matrix[13];
		res.z = matrix[2] * p.x + matrix[6] * p.y + matrix[10] * p.z
				+ matrix[14];
		res.w = matrix[3] * p.x + matrix[7] * p.y + matrix[11] * p.z
				+ matrix[15];
		return res;
	}

	/**
	 * Multiplica el punto por la matriz dejando el resultado en el punto
	 * indicado.
	 * 
	 * @param m
	 *            Matriz por al que multiplicar el punto.
	 * @param p
	 *            Punto a transformar.
	 */
	public static void mult(N3Matrix4D m, N3Point3D p) {
		float x, y, z, w;
		x = m.matrix[0] * p.x + m.matrix[4] * p.y + m.matrix[8] * p.z
				+ m.matrix[12];
		y = m.matrix[1] * p.x + m.matrix[5] * p.y + m.matrix[9] * p.z
				+ m.matrix[13];
		z = m.matrix[2] * p.x + m.matrix[6] * p.y + m.matrix[10] * p.z
				+ m.matrix[14];
		w = m.matrix[3] * p.x + m.matrix[7] * p.y + m.matrix[11] * p.z
				+ m.matrix[15];
		p.x = x;
		p.y = y;
		p.z = z;
		p.w = w;
	}

	/**
	 * Multiplica el vector por la matriz dejando el resultado en el vector
	 * indicado.
	 * 
	 * @param m
	 *            Matriz por al que multiplicar el vector.
	 * @param v
	 *            Vector a transformar.
	 */
	public static void mult(N3Matrix4D m, N3Vector3D v) {
		float x, y, z;
		x = m.matrix[0] * v.x + m.matrix[4] * v.y + m.matrix[8] * v.z;
		y = m.matrix[1] * v.x + m.matrix[5] * v.y + m.matrix[9] * v.z;
		z = m.matrix[2] * v.x + m.matrix[6] * v.y + m.matrix[10] * v.z;
		v.x = x;
		v.y = y;
		v.z = z;
	}

	/**
	 * Obtiene las componentes de la matriz que indican la posición en el
	 * espacio 3D.
	 * 
	 * @return Posición
	 */
	public N3Point3D getPosition() {
		return new N3Point3D(matrix[12], matrix[13], matrix[14]);
	}

	/**
	 * Obtiene una representacion en forma de String del objeto.
	 * 
	 * @return Representacion en forma de String
	 */
	public String toString() {
		String str = new String();
		for (int i = 0; i < 16; i += 4)
			str += "| " + matrix[i] + " , " + matrix[i + 1] + " , "
					+ matrix[i + 2] + " , " + matrix[i + 3] + " |\n";
		return str;
	}
}
