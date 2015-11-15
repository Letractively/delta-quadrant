# Introduction #

Kadabra is an application to mirror a source folder to a destination folder. You can create multiple projects wich are stored in an SQLite database. With the command status [id](id.md) you can see wich changes are made and with release [id](id.md) you update the changes. You can use different handler like System or FTP.

Kadabra can be used in a staging system i.e. if you have a development server you can copy with Kadabra all files to the test server and from this to the production server. You can also use Kadabra to simply upload local files to an FTP server. Kadabra uploads only the files wich have changed the change is determined based on an XML file wich contains the complete folder structure and md5 hashes of all files.