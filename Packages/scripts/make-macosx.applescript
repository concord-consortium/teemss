set cvs_path to ""tell application "Finder"	set cvs_path to home as string	set cvs_path to cvs_path & "cvsroot:" as string	set release_path to cvs_path & "releases:" as stringend telltell application "StuffIt Deluxe"	activate	set archivename to "CCProbe"	set archive_sit to archivename & ".sit"	set archive_sea to archivename & ".sea"	set destination_path to cvs_path & "releases:download:ccprobe-macosx:"	set source_path to cvs_path & "releases:complete:ccprobe-macosx"	make new archive with properties {location:file (destination_path & archive_sit)}	stuff {alias (source_path)} into archive archive_sit compression level maximum with replacing	make self extracting {alias (destination_path & archive_sit)}	close window (archive_sit)	encode {alias (destination_path & archive_sea)} with binhex into file destination_pathend telltell application "Finder"	delete (destination_path & archive_sit)end telltell application "StuffIt Deluxe"	quitend tell