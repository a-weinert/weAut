# A makefile include for raspberry projects 

# This file contains FTP credentials for one concrete Raspberry target
# device for loading binaries and else from the development workstation.

# It is needed when those differ from the standard pi:raspberry. To keep
# those secret such file shuold never be put under (public) version control.
# This example file is the only exception.

# Copyright  2018  Albrecht Weinert    < a-weinert.de >

ifndef COPYRIGHT_YEAR
$(error ftp include for raspi61 used directly.)
endif
FTPuser = pi:raspberry