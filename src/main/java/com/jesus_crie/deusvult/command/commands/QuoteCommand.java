package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.S;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.ErrorResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuoteCommand extends Command {

    public QuoteCommand() {
        super("quote",
                S.COMMAND_QUOTE_HELP.get(),
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.LONG
                }, this::onCommandQuoteId),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandSearch)
        );
    }

    private boolean onCommandSearch(MessageReceivedEvent event, List<Object> args) {
        String search = String.join(" ", args.stream()
                .map(Object::toString)
                .toArray(String[]::new))
                .toLowerCase();

        int loop = 0;
        for (Message m : event.getTextChannel().getIterableHistory()) {
            if (m.getRawContent().toLowerCase().contains(search))
                return quote(event, m);

            if (loop >= 200)
                break;

            loop++;
        }

        ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.COMMAND_QUOTE_ERROR.get()))
                .send(event.getChannel()).queue();
        return true;
    }

    private boolean onCommandQuoteId(MessageReceivedEvent event, List<Object> args) {
        long id = (long) args.get(0);
        try {
            return quote(event, event.getChannel().getMessageById(id).complete());
        } catch (Exception e) {
            Logger.COMMAND.get().debug("error quote id");
            ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.COMMAND_QUOTE_ERROR.get()))
                    .send(event.getChannel()).queue();
            return true;
        }
    }

    private boolean quote(MessageReceivedEvent event, Message toQuote) {
        ResponseBuilder.create(event.getMessage())
                .setTitle(S.COMMAND_QUOTE_TITLE.format(toQuote.getAuthor().getName(),
                        toQuote.getCreationTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        toQuote.getCreationTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
                .setIcon(toQuote.getAuthor().getEffectiveAvatarUrl())
                .setDescription(toQuote.getRawContent())
                .send(event.getChannel()).queue();

        return true;
    }
}