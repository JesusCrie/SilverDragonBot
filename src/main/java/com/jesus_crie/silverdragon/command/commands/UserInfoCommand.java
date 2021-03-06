package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.jesus_crie.silverdragon.utils.S.f;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        super("userinfo",
                "Donne des informations sur un utilisateur.",
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.ALL_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.USER
                }, this::onCommandGeneric, "<membre>"),

                new CommandPattern(null, (e, a) -> onCommandSelf(e), "")
        );
    }

    private boolean onCommandGeneric(MessageReceivedEvent event, List<Object> args) {
        return print(event, event.getGuild().getMember((User) args.get(0)));
    }

    private boolean onCommandSelf(MessageReceivedEvent event) {
        return print(event, event.getMember());
    }

    private boolean print(MessageReceivedEvent event, Member member) {
        ResponseBuilder.create(event.getMessage())
                .setTitle(StringUtils.stringifyUser(member.getUser()) + (member.getNickname() == null ? "" : f(" (%s)", member.getNickname())))
                .setIcon(member.getUser().getEffectiveAvatarUrl())
                .setThumbnail(member.getUser().getEffectiveAvatarUrl())
                .setColor(member.getColor())
                .addField("ID", member.getUser().getId(), true)
                .addField("Access Level", AccessLevel.fromMember(member).name(), true)
                .addField("Status", member.getOnlineStatus().getKey().toUpperCase(), true)
                .addField("Jeux", member.getGame() == null ? "None" : member.getGame().getName(), true)
                .addField("A rejoint le serveur", member.getJoinDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), true)
                .addField("Roles", String.join(", ",
                        member.getRoles().stream()
                        .map(Role::getAsMention)
                        .toArray(String[]::new)), false)
                .send(event.getChannel()).queue();

        return true;
    }
}
