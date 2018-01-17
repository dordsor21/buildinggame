## Introduction

Thanks for wanting to help with this project, we appreciate you taking your time to help improve this project.
In order to make this as easy and as enjoyable as possible, please read the following guidelines.

## Ground rules

Before you make a contribution, please make sure it follows these ground rules:

### Ground rules for issues

* Make sure you're using the latest version, chances are that the problem has already been solved.
* Check if there's already an issue made (which is the same as yours) before you make another one.
* Don't post your issue in other issues, please keep everything nicely organized and make a separate one.
* Don't ask for support for outdated Minecraft versions, we won't add it.
* Please keep the issues section for actual bugs or code problems, not for support. You can ask support on
[SpigotMC](https://www.spigotmc.org/threads/building-game.83557/) and [DevBukkit](https://dev.bukkit.org/projects/buildinggame#comments).

### Ground rules for pull requests

* Make sure your pull request benefits all users, not just you. If you want to add features specific for you or your server,
please consider maintaining a fork of the project or using the [API](https://github.com/stefvanschie/buildinggame/wiki/API).
* Make sure you're pull request hasn't already been made.
* If you're making a pull request regarding an issue, please link that issue as well.
* Follow the [K&R Java](https://en.wikipedia.org/wiki/Indent_style#Variant:_Java) code style.
* Check and fix any warnings/errors that your IDE has given you, before submitting the pull request.

## First timers

If you want to help with this project, but don't know where to start, take a look at the
[currently opened issues](https://github.com/stefvanschie/buildinggame/issues). There are always some people who could use your help.

If you're wanting to make a pull request which involves settings (in either the config.yml / messages.yml), I highly recommend you to take
a look at [this specific class](https://github.com/stefvanschie/buildinggame/blob/master/buildinggame/src/com/gmail/stefvanschiedev/buildinggame/managers/files/SettingsManager.java).
In short this will manage all your settings. So no more hassle with config#isSet or config#addDefault, that will already been handled for you.
You just have to add the settings to either the default config.yml or messages.yml and can continue programming.

## Getting started with pull requests

If your pull request is bigger than a few lines (say 10 max.) make a fork of the project and add the changes there first. Changes
that are smaller (documentation changes, spelling/grammer fixes, adding comments, constant values errors, additional logging messages,
etc.) can be done direclty without the need for a new fork.

## Submitting an issue

When submitting an issue, a template will be shown. Please don't discard this, but instead fill in the information asked for. There's
room at the bottom for additional info you'd like to include, in case needed. This template is for users submitting a bug. If you aren't
reporting a bug, but rather suggesting code improvements, pointing out spelling mistakes etc, you can discard the template.