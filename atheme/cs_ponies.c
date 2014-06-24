/*
 * Copyright (c) 2014 PonyChat
 * (https://github.com/PonyChat/ponychat-atheme-modules/blob/master/cs_ponies.c)
 *
 * Module which allows users to retrieve episode information about My Little Pony: Friendship is Magic.
 */

#include "atheme.h"

DECLARE_MODULE_V1
(
        "contrib/cs_ponies", true, _modinit, _moddeinit,
        PACKAGE_STRING,
        "HangoutsNET Network <https://chat.parted.me/>"
);

static void cs_cmd_countdown(sourceinfo_t *si, int parc, char *parv[]);
static void cs_cmd_episode(sourceinfo_t *si, int parc, char *parv[]);
static void cs_cmd_shuffle(sourceinfo_t *si, int parc, char *parv[]);

static void write_fimdb(database_handle_t *db);
static void db_h_fim(database_handle_t *db, const char *type);

command_t cs_episode = { "EPISODE", N_("Manage or view the list of My Little Pony: Friendship is Magic episodes."), PRIV_USER_ADMIN, 5, cs_cmd_episode, { .path = "contrib/cs_episode" } };
command_t cs_countdown = { "COUNTDOWN", N_("Responds with the time remaining until the next episode of My Little Pony: Friendship is Magic"), AC_NONE, 3, cs_cmd_countdown, { .path = "contrib/cs_countdown" } };
command_t cs_shuffle = { "SHUFFLE", N_("Responds with the name of a randomly picked episode of My Little Pony: Friendship is Magic"), AC_NONE, 0, cs_cmd_shuffle, { .path = "contrib/cs_shuffle" } };

struct episode_ {
        char *title;
        unsigned short int season;
        unsigned short int number;
        time_t air_ts;
};

typedef struct episode_ episode_t;

mowgli_list_t cs_episodelist;
mowgli_random_t *r;

void _modinit(module_t *m)
{
        if (!module_find_published("backend/opensex"))
        {
                slog(LG_INFO, "Module %s requires use of the OpenSEX database backend, refusing to load.", m->name);
                m->mflags = MODTYPE_FAIL;
                return;
        }

        hook_add_db_write(write_fimdb);

        db_register_type_handler("FIM", db_h_fim);

        service_named_bind_command("chanserv", &cs_countdown);
        service_named_bind_command("chanserv", &cs_episode);
        service_named_bind_command("chanserv", &cs_shuffle);

        r = mowgli_random_create_with_seed(time(NULL));
}

void _moddeinit(module_unload_intent_t intent)
{
        hook_del_db_write(write_fimdb);

        db_unregister_type_handler("FIM");

        service_named_unbind_command("chanserv", &cs_countdown);
        service_named_unbind_command("chanserv", &cs_episode);
        service_named_unbind_command("chanserv", &cs_shuffle);

        mowgli_free(r);
}

static void write_fimdb(database_handle_t *db)
{
        mowgli_node_t *n;

        MOWGLI_ITER_FOREACH(n, cs_episodelist.head)
        {
                episode_t *l = n->data;

                db_start_row(db, "FIM");
                db_write_time(db, l->air_ts);
                db_write_uint(db, l->season);
                db_write_uint(db, l->number);
                db_write_str(db, l->title);
                db_commit_row(db);
        }
}

static void db_h_fim(database_handle_t *db, const char *type)
{
        time_t air_ts = db_sread_time(db);
        unsigned short int season = db_sread_uint(db);
        unsigned short int number  = db_sread_uint(db);
        const char *title = db_sread_str(db);

        episode_t *l = smalloc(sizeof(episode_t));
        l->air_ts = air_ts;
        l->season = season;
        l->number = number;
        l->title = sstrdup(title);
        mowgli_node_add(l, mowgli_node_create(), &cs_episodelist);
}

static void cs_cmd_shuffle(sourceinfo_t *si, int parc, char *parv[])
{
        int epnum = MOWGLI_LIST_LENGTH(&cs_episodelist) - 1;
        int randnum = mowgli_random_int_ranged(r, 0, epnum);

        if (si->c == NULL)
        {
                command_success_nodata(si, _("This command must be used in channels."));
                return;
        }

        episode_t *toSee;
        mowgli_node_t *episode = mowgli_node_nth(&cs_episodelist, randnum);
        service_t *svs = service_find("chanserv");

        if(episode != NULL)
        {
                toSee = episode->data;

                msg(svs->me->nick, si->c->name, "Season %d Episode %d: \2\"%s\"\2",
                        toSee->season, toSee->number, toSee->title);
                return;
        }

        msg(svs->me->nick, si->c->name,
                "There are currently no episodes in the episode database.");
}

static void cs_cmd_countdown(sourceinfo_t *si, int parc, char *parv[])
{
        char buf[BUFSIZE];
        struct tm tm;
        mowgli_node_t *n;
        episode_t *l;

        time_t diff;
        time_t last_diff = CURRTIME;
        bool found = false;
        size_t target_node;
        size_t i = 0;

        if (si->c == NULL)
                return;

        /* TODO: this whole thing sucks -- rewrite */

        service_t *svs = service_find("chanserv");

        if (parc == 3)
        {
                unsigned short int season_i = atoi(parv[1]);
                unsigned short int episode_i = atoi(parv[2]);

                MOWGLI_ITER_FOREACH(n, cs_episodelist.head)
                {
                        l = n->data;

                        ++i;

                        if (l->season == season_i && l->number == episode_i)
                        {
                                found = true;
                                target_node = i - 1;
                                break;
                        }
                }

                if (!found)
                        msg(svs->me->nick, si->c->name,
                                        "Air time of season %d episode %d is unknown",
                                        season_i, episode_i);
        }
        else
        {
                MOWGLI_ITER_FOREACH(n, cs_episodelist.head)
                {
                        l = n->data;

                        ++i;

                        if (CURRTIME > l->air_ts)
                                continue;

                        diff = l->air_ts - CURRTIME;


                        slog(LG_DEBUG, "cs_cmd_countdown(): N: %d D: %d L: %d A: %d S: %d E: %d",
                                        CURRTIME, diff, last_diff, l->air_ts, l->season, l->number);

                        if (diff < last_diff)
                        {

                                last_diff = diff;
                                target_node = i - 1;
                                found = true;

                                slog(LG_DEBUG, "cs_cmd_countdown(): New target selected: %d",
                                                target_node);
                        }

                }

                if (!found)
                        msg(svs->me->nick, si->c->name,
                                        "Next episode air time is unknown.");

        }

        if (found)
        {
                n = mowgli_node_nth(&cs_episodelist, target_node);
                l = n->data;

                tm = *gmtime(&l->air_ts);
                diff = l->air_ts - CURRTIME;

                unsigned int weeks      = abs(diff / 60 / 60 / 24 / 7);
                unsigned int days       = abs(diff / 60 / 60 / 24 % 7);
                unsigned int hours      = abs(diff / 60 / 60 % 24);
                unsigned int minutes    = abs(diff /60 % 60);
                unsigned int seconds    = abs(diff % 60);

                strftime(buf, BUFSIZE, TIME_FORMAT, &tm);

                if (diff < 0)
                        msg(svs->me->nick, si->c->name,
                                        "Episode %d of season %d \2\"%s\"\2 aired %d weeks, %d days, %d hours, %d minutes, %d seconds ago (%s UTC)",
                                        l->number, l->season, l->title, weeks, days, hours, minutes, seconds, buf);
                else
                        msg(svs->me->nick, si->c->name,
                                        "Episode %d of season %d \2\"%s\"\2 airs in %d weeks, %d days, %d hours, %d minutes, %d seconds (%s UTC)",
                                        l->number, l->season, l->title, weeks, days, hours, minutes, seconds, buf);

                return;
        }

        return;
}


static void cs_cmd_episode(sourceinfo_t *si, int parc, char *parv[])
{
        char *action = parv[0];
        char *season = parv[1];
        char *episode = parv[2];
        char *datetime = parv[3];
        char *title = parv[4];
        mowgli_node_t *n, *tn;
        episode_t *l;

        struct tm ep_tm;

        if (si->c != NULL)
        {
                command_success_nodata(si, _("This command cannot be used in channels."));
                return;
        }

        if (si->smu == NULL)
        {
                command_fail(si, fault_noprivs, _("You are not logged in."));
                return;
        }

        if (!action)
        {
                command_fail(si, fault_needmoreparams, STR_INSUFFICIENT_PARAMS, "EPISODE");
                command_fail(si, fault_needmoreparams, _("Syntax: EPISODE <action> <parameters>"));
                return;
        }

        if (!strcasecmp("ADD", action))
        {
                if (!title)
                {
                        command_fail(si, fault_needmoreparams, STR_INSUFFICIENT_PARAMS, "EPISODE");
                        command_fail(si, fault_needmoreparams, _("Syntax: EPISODE ADD <season> <episode> <UNIX timestamp> <title>"));
                        return;
                }

                unsigned short int episode_i = atoi(episode);
                unsigned short int season_i = atoi(season);

                //strptime(datetime, "%Y%m%d%H%M%S", &ep_tm);
                strptime(datetime, "%s", &ep_tm);

                if (episode_i == 0 || season_i == 0 || &ep_tm == NULL)
                {
                        command_fail(si, fault_needmoreparams, STR_INVALID_PARAMS, "EPISODE");
                        command_fail(si, fault_needmoreparams, _("Syntax: EPISODE ADD <season> <episode> <UNIX timestamp> <title>"));
                        return;
                }


                time_t time = mktime(&ep_tm);

                /* search for it */
                MOWGLI_ITER_FOREACH(n, cs_episodelist.head)
                {
                        l = n->data;

                        if (l->season == season_i && l->number == episode_i)
                        {
                                command_success_nodata(si, _("Episode \2%d\2 of season \2%d\2 is already in the episode list."), episode_i, season_i);
                                return;
                        }
                }

                l = smalloc(sizeof(episode_t));
                l->number = episode_i;
                l->season = season_i;
                l->air_ts = time;
                l->title = sstrdup(title);

                logcommand(si, CMDLOG_ADMIN, "EPISODE:ADD: \2%d %d\2 (Title: \2%s\2)", season_i, episode_i, title);

                n = mowgli_node_create();
                mowgli_node_add(l, n, &cs_episodelist);

                command_success_nodata(si, _("Episode \2%d\2 of season \2%d\2 has been added to the episode list."), episode_i, season_i);
                return;
        }
        else if (!strcasecmp("DEL", action))
        {
                if (!episode)
                {
                        command_fail(si, fault_needmoreparams, STR_INSUFFICIENT_PARAMS, "EPISODE");
                        command_fail(si, fault_needmoreparams, _("Syntax: EPISODE DEL <season> <episode>"));
                        return;
                }

                unsigned short int episode_i = atoi(episode);
                unsigned short int season_i = atoi(season);

                if (episode_i == 0 || season_i == 0)
                {
                        command_fail(si, fault_needmoreparams, STR_INVALID_PARAMS, "EPISODE");
                        command_fail(si, fault_needmoreparams, _("Syntax: EPISODE DEL <season> <episode>"));
                        return;
                }

                MOWGLI_ITER_FOREACH_SAFE(n, tn, cs_episodelist.head)
                {
                        l = n->data;

                        if (l->number == episode_i && l->season == season_i)
                        {
                                logcommand(si, CMDLOG_ADMIN, "EPISODE:DEL: \2%d %d\2", l->season, l->number);

                                mowgli_node_delete(n, &cs_episodelist);

                                free(l->title);
                                free(l);

                                command_success_nodata(si, _("Episode \2%d\2 of season \2%d\2 has been deleted from the episode list."), episode_i, season_i);

                                return;
                        }
                }
                command_success_nodata(si, _("Episode \2%d\2 of season \2%d\2 was not found in the episode list."), episode_i, season_i);
                return;
        }
        else if (!strcasecmp("LIST", action))
        {
                char buf[BUFSIZE];
                struct tm tm;

                MOWGLI_ITER_FOREACH(n, cs_episodelist.head)
                {
                        l = n->data;

                        tm = *gmtime(&l->air_ts);
                        strftime(buf, BUFSIZE, TIME_FORMAT, &tm);
                        command_success_nodata(si, "Season: \2%d\2, Episode: \2%d\2, Title: \2%s\2 (%s)",
                                l->season, l->number, l->title, buf);
                }
                command_success_nodata(si, "End of list.");
                logcommand(si, CMDLOG_GET, "EPISODE:LIST");
                return;
        }
        else
        {
                command_fail(si, fault_badparams, STR_INVALID_PARAMS, "EPISODE");
                return;
        }
}

/* vim:cinoptions=>s,e0,n0,f0,{0,}0,^0,=s,ps,t0,c3,+s,(2s,us,)20,*30,gs,hs
 * vim:ts=8
 * vim:sw=8
 * vim:noexpandtab
 */
