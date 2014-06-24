/*
 * Copyright (c) 2014 Justin Kaufman 
 *
 * Module which allows users to be hugged by ChanServ.
 *
 */

#include "atheme.h"

DECLARE_MODULE_V1
(
        "contrib/cs_boop", false, _modinit, _moddeinit,
        PACKAGE_STRING,
        "Justin Kaufman <akaritakai@gmail.com>"
);

static void cs_cmd_boop(sourceinfo_t *si, int parc, char *parv[]);

command_t cmd_boop = { "BOOP", N_("Boops a user."), AC_NONE, 2, cs_cmd_boop, { .path = "contrib/cs_boop" } };

void _modinit(module_t * m)
{
        service_named_bind_command("chanserv", &cmd_boop);
}

void _moddeinit(module_unload_intent_t intent)
{
        service_named_unbind_command("chanserv", &cmd_boop);
}

static void cs_cmd_boop(sourceinfo_t *si, int parc, char *parv[])
{
        char *user = parv[1];

        if (si->c == NULL)
        {
                command_success_nodata(si, _("This command must be used in channels."));
                return;
        }

        service_t *svs = service_find("chanserv");

        srand(time(NULL));

        if (parc == 1)
        {
                msg(svs->me->nick, si->c->name,
                        "\001ACTION boops %s\001", si->su->nick);
        }
        else
        {
                msg(svs->me->nick, si->c->name,
                        "\001ACTION boops %s\001", user);
        }
}
