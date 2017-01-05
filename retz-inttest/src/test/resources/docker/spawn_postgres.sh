#!/bin/bash
#
#    Retz
#    Copyright (C) 2016-2017 Nautilus Technologies, Inc.
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#


set -e
set -x

## Init and start postgresql
## mkdir -p /var/lib/postgres
## chown postgres:postgres /var/lib/postgres
## /usr/lib/postgresql/9.5/bin/initdb -D /var/lib/postgres
## /usr/lib/postgresql/9.5/bin/pg_ctl -D /var/lib/postgres -l /build/log/postgres.log start
chmod 777 /build/log
su -m postgres -c "initdb -D /var/lib/pgsql/data"
su -m postgres -c "pg_ctl -D /var/lib/pgsql/data -l /build/log/postgres.log start"
sleep 5
su -m postgres -c "createuser retz"
su -m postgres -c "createdb -O retz retz"
