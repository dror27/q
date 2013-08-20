/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

DROP TABLE IF EXISTS `keywords`;
CREATE TABLE `keywords` (

  `id` integer NOT NULL,
  `keyword` char(64) NOT NULL,
  
  PRIMARY KEY  (`id`)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `keywords` DISABLE KEYS */;
/*!40000 ALTER TABLE `keywords` ENABLE KEYS */;


DROP TABLE IF EXISTS `qs`;
CREATE TABLE `qs` (

  `id` integer NOT NULL,
  `q` char(64) NOT NULL,
  `data_type` char(64),
  `text_data` TEXT,
  `binary_data` MEDIUMBLOB,
  `content_type` char(64),
  
  `latitude` double,
  `longitude` double,
  `altitude` double,
  
  `version` integer,
  `lease_holder` char(64),
  `lease_started_at` DATETIME,
  `lease_ends_at` DATETIME 
  
  PRIMARY KEY  (`id`)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `keywords` DISABLE KEYS */;
/*!40000 ALTER TABLE `keywords` ENABLE KEYS */;




/* ======================
 * Below tables are generic
 * ======================
 */

/* an account is an entity which represents an organization or an individual registred to use the service. It is externally managed by the UI */
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (

  `id` integer NOT NULL,
  `display_name` char(64) NOT NULL,
  `role` integer,
  
  PRIMARY KEY  (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;

/* a user is represents a person or login credentials to use the system. a user always belongs to an account */
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (

  `id` integer NOT NULL,
  `display_name` char(64),
  `account_id` integer,
  `role` integer,
 
  `username` char(64),
  `password` char(64),

  `time_zone` char(64),

  PRIMARY KEY  (`id`),
  UNIQUE KEY `USERNAME` (`username`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/* a job is a long running process */
DROP TABLE IF EXISTS `jobs`;
CREATE TABLE `jobs` (

  `id` integer NOT NULL,
  `display_name` char(64) NOT NULL,
  `kind` char(16) NOT NULL,					/* download,upload,predict */ 
  `status` int NOT NULL DEFAULT 0,			/* 0=none/created, 1=pending, 2=running, 3=done, 4=done/error */
  
  `datetime_created` datetime NOT NULL,		/* datetime when created */
  `datetime_pending` datetime,
  `datetime_scheduled` datetime,
  `datetime_started` datetime,
  `datetime_done` datetime,

  `message` char(255),						/* depending of status: progress, done, error ... */
  `progress` double,						/* 0.0 - 1.0 */
  
  `param0` char(255),					
  `param1` char(255),					
  `param2` char(255),					
  `param3` char(255),					

   `datetime_next_run` datetime,			/* datetime for next run */

  PRIMARY KEY  (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;

/* generic reference value: types enumeration defined elsewhere */
DROP TABLE IF EXISTS `refvalues`;
CREATE TABLE `refvalues` (
  `id` int NOT NULL,
  `type` varchar(32) NOT NULL,  
  `display_name` varchar(128) NOT NULL,
  `description` varchar(128),
  `code` varchar(128),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `refvalues` DISABLE KEYS */;
/*!40000 ALTER TABLE `refvalues` ENABLE KEYS */;

/* generic relation */
DROP TABLE IF EXISTS `relations`;
CREATE TABLE `relations` (
  `id` int NOT NULL,
  `type` varchar(16) NOT NULL,  
  `left_id` int NOT NULL,
  `right_id` int NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `relations` DISABLE KEYS */;
/*!40000 ALTER TABLE `relations` ENABLE KEYS */;

/* generic property for an object */
DROP TABLE IF EXISTS `props`;
CREATE TABLE `props` (
  `id` int NOT NULL,
  `name` varchar(128) NOT NULL,  
  `value` varchar(128),
  `object_id` int,
  `object_class` varchar(64),  
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40000 ALTER TABLE `props` DISABLE KEYS */;
/*!40000 ALTER TABLE `props` ENABLE KEYS */;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;


