/*
*	Copyright (c) 2003, 2012 Jorge García, Unai Aguilera
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

package examples;

import nu3a.math.*;
import nu3a.geometry.*;

public class Test {
	public static void main(String args[]) {
		N3Matrix4D m = new N3Matrix4D();
		System.out.println(m);
		N3Point3D p = new N3Point3D(0.15f, 0.15f, 0.15f);
		System.out.println(p);
		N3Point3D p2 = m.mult(p);
		System.out.println(p2);
		System.out.println(p);
	}
}
