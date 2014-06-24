/*
 * Copyright (c) 2014 Justin Kaufman 
 *
 * Module which allows users to be hugged by ChanServ.
 *
 */

#include "atheme.h"

DECLARE_MODULE_V1
(
        "contrib/cs_hug", false, _modinit, _moddeinit,
        PACKAGE_STRING,
        "Justin Kaufman <akaritakai@gmail.com>"
);

static void cs_cmd_hug(sourceinfo_t *si, int parc, char *parv[]);

command_t cmd_hug = { "HUG", N_("Hugs a user."), AC_NONE, 2, cs_cmd_hug, { .path = "contrib/cs_hug" } };

void _modinit(module_t * m)
{
        service_named_bind_command("chanserv", &cmd_hug);
}

void _moddeinit(module_unload_intent_t intent)
{
        service_named_unbind_command("chanserv", &cmd_hug);
}

static void cs_cmd_hug(sourceinfo_t *si, int parc, char *parv[])
{
        char *user = parv[1];

        if (si->c == NULL)
        {
                command_success_nodata(si, _("This command must be used in channels."));
                return;
        }

        static const char *responses[9]= {
                N_("hugs"),
                N_("squeezes"),
                N_("cuddles up with"),
                N_("nuzzles"),
                N_("snuggles"),
                N_("snuggles into"),
                N_("smiles at"),
                N_("giggles at"),
                N_("kisses"),
        };

        service_t *svs = service_find("chanserv");

        srand(time(NULL));

        if (parc == 1)
        {
                msg(svs->me->nick, si->c->name,
                        "\001ACTION %s %s\001", responses[rand() % 9], si->su->nick);
        }
        else
        {
                msg(svs->me->nick, si->c->name,
                        "\001ACTION %s %s\001", responses[rand() % 9], user);
        }
}
