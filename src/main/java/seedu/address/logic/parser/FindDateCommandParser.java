package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.address.logic.commands.FindDateCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.task.DueContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindDateCommand object
 */
public class FindDateCommandParser implements Parser<FindDateCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindDateCommand
     * and returns an FindDateCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindDateCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindDateCommand.MESSAGE_USAGE));
        }

        String[] dateKeywords = trimmedArgs.split("\\s+");

        return new FindDateCommand(new DueContainsKeywordsPredicate(Arrays.asList(dateKeywords)));
    }

}
