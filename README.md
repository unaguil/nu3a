Nu3A
====

Nu3A is a simple 3D library for Java.

The library was developed by the authors as their Computer Engineering 
final year project (2002/2003) at the University of Deusto.

Features
--------

* Hierarchycal engine
* Transformations
* Basic colision detection (bounding boxes)
* Object picking using ray tracing
* Pure software render with:
	* Light support (ambient and spot lights)
	* Textures
	* Basic materials
* Math classes (Vector, Matrix, ...)
* XML persitence of scenes (ad-hoc format)

Examples
--------

1. Compile
	
	ant 

2. Run an example
	
	java -cp dist/nu3a-1.0.jar examples.BasicTest -> Triangle rendering test

	java -cp dist/nu3a-1.0.jar examples.SceneTest -> More complex scene. Press L to set light on/off, mouse to select an object and show its bounding box and cursors to move the box.


License
-------

[GPL v3](http://www.gnu.org/licenses/gpl-3.0.html)

Jorge García <bardok@gmail.com>, Unai Aguilera <gkalgan@gmail.com>

