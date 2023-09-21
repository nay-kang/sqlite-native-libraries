#include "sqlite3.h"

int sqlite3_simple_init(sqlite3 *db, char **pzErrMsg, const sqlite3_api_routines *pApi);

int sqlite3_extra_init(const char *z){
    int rc = SQLITE_OK;
    rc = sqlite3_auto_extension((void (*)(void))sqlite3_simple_init);
    return rc;
}


