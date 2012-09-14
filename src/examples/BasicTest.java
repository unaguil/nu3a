/*
*	Copyright (c) 2003, 2011 Jorge García, Unai Aguilera
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

import javax.swing.JFrame;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import nu3a.material.color.N3ColorRGBA;
import nu3a.geometry.N3Point3D;
import nu3a.render.software.N3SoftwareRenderContext;

public class BasicTest extends Thread {
	N3SoftwareRenderContext c;

	public BasicTest(N3SoftwareRenderContext context) {
		c = context;
		N3ColorRGBA color = new N3ColorRGBA(0, 0, 0);
		c.setDepthTest(true);
		c.cleanZBuffer();
		c.clearToColor(color);
		c.paint();
	}

	public void run() {
		N3Point3D vertexP = new N3Point3D(0, 0, 0);
		N3Point3D vertexL1 = new N3Point3D(0, 0, 0);
		N3Point3D vertexL2 = new N3Point3D(0, 0, 0);
		N3Point3D vertexT1 = new N3Point3D(0, 0, 0);
		N3Point3D vertexT2 = new N3Point3D(0, 0, 0);
		N3Point3D vertexT3 = new N3Point3D(0, 0, 0);
		N3ColorRGBA color = new N3ColorRGBA(0, 0, 0);
		N3ColorRGBA colorG1 = new N3ColorRGBA(0, 0, 0);
		N3ColorRGBA colorG2 = new N3ColorRGBA(0, 0, 0);
		N3ColorRGBA colorG3 = new N3ColorRGBA(0, 0, 0);
		double t = System.currentTimeMillis();
		int i = 0;
		boolean flatVertex = true;
		boolean flatHLine = true;
		boolean flatVLine = true;
		boolean flatLine = true;
		boolean goraudHLine = true;
		boolean goraudVLine = true;
		boolean goraudLine = true;
		boolean flatTriangle = true;
		boolean goraudTriangle = true;
		while (true) {
			c.beginDrawingMode(N3SoftwareRenderContext.N3_POINTS);
			/* Dibujamos un punto */
			if (flatVertex) {
				color.setR((float) Math.random());
				color.setG((float) Math.random());
				color.setB((float) Math.random());
				c.setColor(color);
				vertexP.setX((float) (Math.random() * c.getWidth()));
				vertexP.setY((float) (Math.random() * c.getHeight()));
				vertexP.setZ((float) Math.random());
				c.setVertex(vertexP);
			}
			c.endDrawingMode();

			/* Vamos a dibjuar líneas... */
			c.beginDrawingMode(N3SoftwareRenderContext.N3_LINES);
			/* Dibujamos una línea horizontal flat */
			if (flatHLine) {
				color.setR((float) Math.random());
				color.setG((float) Math.random());
				color.setB((float) Math.random());
				c.setColor(color);
				vertexL1.setX((float) (Math.random() * c.getWidth()));
				vertexL1.setY((float) (Math.random() * c.getHeight()));
				vertexL1.setZ((float) Math.random());
				c.setVertex(vertexL1);
				vertexL2.setX((float) (Math.random() * c.getWidth()));
				vertexL2.setY(vertexL1.getY());
				vertexL2.setZ((float) Math.random());
				c.setVertex(vertexL2);
			}

			/* Dibujamos una línea vertical flat */
			if (flatVLine) {
				color.setR((float) Math.random());
				color.setG((float) Math.random());
				color.setB((float) Math.random());
				c.setColor(color);
				vertexL1.setX((float) (Math.random() * c.getWidth()));
				vertexL1.setY((float) (Math.random() * c.getHeight()));
				vertexL1.setZ((float) Math.random());
				c.setVertex(vertexL1);
				vertexL2.setX(vertexL1.getX());
				vertexL2.setY((float) (Math.random() * c.getHeight()));
				vertexL2.setZ((float) Math.random());
				c.setVertex(vertexL2);
			}

			/* Dibujamos una línea cualquiera flat */
			if (flatLine) {
				color.setR((float) Math.random());
				color.setG((float) Math.random());
				color.setB((float) Math.random());
				c.setColor(color);
				vertexL1.setX((float) (Math.random() * c.getWidth()));
				vertexL1.setY((float) (Math.random() * c.getHeight()));
				vertexL1.setZ((float) Math.random());
				c.setVertex(vertexL1);
				vertexL2.setX((float) (Math.random() * c.getWidth()));
				vertexL2.setY((float) (Math.random() * c.getHeight()));
				vertexL2.setZ((float) Math.random());
				c.setVertex(vertexL2);
			}

			/* Dibujamos una línea horizontal goraud */
			if (goraudHLine) {
				colorG1.setR((float) Math.random());
				colorG1.setG((float) Math.random());
				colorG1.setB((float) Math.random());
				c.setColor(colorG1);
				vertexL1.setX((float) (Math.random() * c.getWidth()));
				vertexL1.setY((float) (Math.random() * c.getHeight()));
				vertexL1.setZ((float) Math.random());
				c.setVertex(vertexL1);
				colorG2.setR((float) Math.random());
				colorG2.setG((float) Math.random());
				colorG2.setB((float) Math.random());
				c.setColor(colorG2);
				vertexL2.setX((float) (Math.random() * c.getWidth()));
				vertexL2.setY(vertexL1.getY());
				vertexL2.setZ((float) Math.random());
				c.setVertex(vertexL2);
			}

			/* Dibujamos una línea vertical goraud */
			if (goraudVLine) {
				colorG1.setR((float) Math.random());
				colorG1.setG((float) Math.random());
				colorG1.setB((float) Math.random());
				c.setColor(colorG1);
				vertexL1.setX((float) (Math.random() * c.getWidth()));
				vertexL1.setY((float) (Math.random() * c.getHeight()));
				vertexL1.setZ((float) Math.random());
				c.setVertex(vertexL1);
				colorG2.setR((float) Math.random());
				colorG2.setG((float) Math.random());
				colorG2.setB((float) Math.random());
				c.setColor(colorG2);
				vertexL2.setX(vertexL1.getX());
				vertexL2.setY((float) (Math.random() * c.getHeight()));
				vertexL2.setZ((float) Math.random());
				c.setVertex(vertexL2);
			}

			/* Dibujamos una línea goraud */
			if (goraudLine) {
				colorG1.setR((float) Math.random());
				colorG1.setG((float) Math.random());
				colorG1.setB((float) Math.random());
				c.setColor(colorG1);
				vertexL1.setX((float) (Math.random() * c.getWidth()));
				vertexL1.setY((float) (Math.random() * c.getHeight()));
				vertexL1.setZ((float) Math.random());
				c.setVertex(vertexL1);
				colorG2.setR((float) Math.random());
				colorG2.setG((float) Math.random());
				colorG2.setB((float) Math.random());
				c.setColor(colorG2);
				vertexL2.setX((float) (Math.random() * c.getWidth()));
				vertexL2.setY((float) (Math.random() * c.getHeight()));
				vertexL2.setZ((float) Math.random());
				c.setVertex(vertexL2);
			}

			c.endDrawingMode();

			c.beginDrawingMode(N3SoftwareRenderContext.N3_TRIANGLES);
			/* Dibujamos un triángulo plano */
			if (flatTriangle) {
				color.setR((float) Math.random());
				color.setG((float) Math.random());
				color.setB((float) Math.random());
				c.setColor(color);
				vertexT1.setX((float) (Math.random() * c.getWidth() + 10));
				vertexT1.setY((float) (Math.random() * c.getHeight() - 10));
				vertexT1.setZ((float) Math.random());
				c.setVertex(vertexT1);
				vertexT2.setX((float) (Math.random() * c.getWidth() - 10));
				vertexT2.setY((float) (Math.random() * c.getHeight() + 10));
				vertexT2.setZ((float) Math.random());
				c.setVertex(vertexT2);
				vertexT3.setX((float) (Math.random() * c.getWidth() + 10));
				vertexT3.setY((float) (Math.random() * c.getHeight() - 10));
				vertexT3.setZ((float) Math.random());
				c.setVertex(vertexT3);
			}

			/* Dibujamos un triángulo goraud */
			if (goraudTriangle) {
				colorG1.setR((float) Math.random());
				colorG1.setG((float) Math.random());
				colorG1.setB((float) Math.random());
				c.setColor(colorG1);
				vertexT1.setX((float) (Math.random() * c.getWidth()));
				vertexT1.setY((float) (Math.random() * c.getHeight()));
				vertexT1.setZ((float) Math.random());
				c.setVertex(vertexT1);
				colorG2.setR((float) Math.random());
				colorG2.setG((float) Math.random());
				colorG2.setB((float) Math.random());
				c.setColor(colorG2);
				vertexT2.setX((float) (Math.random() * c.getWidth()));
				vertexT2.setY((float) (Math.random() * c.getHeight()));
				vertexT2.setZ((float) Math.random());
				c.setVertex(vertexT2);
				colorG3.setR((float) Math.random());
				colorG3.setG((float) Math.random());
				colorG3.setB((float) Math.random());
				c.setColor(colorG3);
				vertexT3.setX((float) (Math.random() * c.getWidth()));
				vertexT3.setY((float) (Math.random() * c.getHeight()));
				vertexT3.setZ((float) Math.random());
				c.setVertex(vertexT3);
			}

			c.endDrawingMode();

			c.paint();
			i++;
			if ((System.currentTimeMillis() - t) >= 5000) {
				System.out.println(i + " frames in 5 seconds: " + i / 5 + " FPS.");
				i = 0;
				t = System.currentTimeMillis();
			}
			/*
			 * try { sleep(40); } catch (InterruptedException ie){}
			 */
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("Prueba software");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Panel p = new Panel();
		f.getContentPane().add(p);
		f.setSize(320, 240);
		f.setVisible(true);
		N3SoftwareRenderContext c = new N3SoftwareRenderContext(p, true);
		BasicTest test = new BasicTest(c);
		test.start();
	}
}
