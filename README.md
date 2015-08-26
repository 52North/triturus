# 52°North Triturus
README file for the [52°North Triturus library][1]

Triturus is a package of Java classes that offer basic functionality to model objects in 3-D space and to set-up simple 3-D scene descriptions. 

## Basic functionality:
* Basic feature geometries (3-D point sets, line-strings, elevation grids, TINs etc.)
* Gridding algorithms for scattered data points
* Readers for various data formats (e.g., XYZ files and Arc/Info ASCII grids)
* Simple file export (e.g., ASCII files, VRML/X3D/X3DOM scene descriptions)
* Generation of simple POV-Ray scene descriptions
* Cross-section generation for elevation grid models 

## Characteristics
* Development platform: Java (Windows and linux/unix)
* Rendering pipeline (filter, mapper, renderer) as the reference model
* Abstraction of concrete (geo-)datasources and renderers through interfaces
* Integration of basic I/O-classes and renderers (3d realtime rendering and ray tracing)
* Freely available implementation examples using this framework: Web Terrain Service (OGC-WTS for LOD 0), profile service for the generation of terrain cross sections, web application for WTS visualization using OGC-WMS as drape texture.

## Development goals
The community's vision is to establish a creative surrounding, which allows efficient and sustainable development of innovative software solutions in the context of 3d modeling and 3d geovisualization.

Concrete development goals are:
* to provide a surrounding for 3D geovisualization tasks, broadly usable in different fields
* to have open ended software that is easily extensible
* to be compatible to existing standards in the context of geo object modeling and computer graphics (OGC, ISO, W3C).

## Installation
To compile the Triturus library, simply execute the following [Maven](http://maven.apache.org/) task 

`mvn clean install`

To use the Triturus library, just add the Triturus JAR file to your Java project.

## License information
This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License version 2 as published
by the Free Software Foundation.

For further information please refer to 'LICENSE'-file

## Additional documents and links
This sections lists documents that lead to a deeper understanding of the Triturus library and give additional information

* White Paper of Triturus: http://52north.org/images/stories/52n/communities/3D/triturus%20white%20paper.pdf 
* 52°North 3D Community Wiki: https://wiki.52north.org/bin/view/V3d/ 
* Triturus Wiki: https://wiki.52north.org/bin/view/V3d/Triturus 

## Contributing

Please find information for Contributing to the project in the seperate [CONTRIBUTE.md](CONTRIBUTE.md).

## Support and Contact

You can get support in the community mailing list and forums:

    http://52north.org/resources/mailing-lists-and-forums/

If you encounter any issues with the software or if you would like to see
certain functionality added, let us know at:

 - Benno Schmidt (b.schmidt@52north.org)
 - Martin May (m.may@52north.org)
 - Christian Danowski (christian.danowski@hs-bochum.de)

The 3D Community

52°North Inititative for Geospatial Open Source Software GmbH, Germany

--
[1]: http://52north.org/communities/3d/triturus
