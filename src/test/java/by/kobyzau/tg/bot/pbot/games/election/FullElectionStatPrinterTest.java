package by.kobyzau.tg.bot.pbot.games.election;

import by.kobyzau.tg.bot.pbot.checker.BotActionAbstractTest;
import by.kobyzau.tg.bot.pbot.checker.BotTypeBotActionChecker;
import by.kobyzau.tg.bot.pbot.checker.ContainsTextBotActionChecker;
import by.kobyzau.tg.bot.pbot.games.election.stat.ElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.games.election.stat.impl.FullElectionStatPrinter;
import by.kobyzau.tg.bot.pbot.model.Pair;
import by.kobyzau.tg.bot.pbot.model.Pidor;
import by.kobyzau.tg.bot.pbot.program.text.*;
import by.kobyzau.tg.bot.pbot.program.text.pidor.ShortNameLinkedPidorText;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.PidorService;
import by.kobyzau.tg.bot.pbot.tg.action.ChatActionBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class FullElectionStatPrinterTest extends BotActionAbstractTest {

  @InjectMocks private ElectionStatPrinter printer = new FullElectionStatPrinter();

  @Mock private PidorService pidorService;

  @Mock private ElectionService electionService;

  private final long chatId = 123;

  @Test
  public void printInfo_withoutCount_noZeros() {
    // given
    List<Pair<Integer, Integer>> pidorIdsAndVotes =
        Arrays.asList(new Pair<>(1, 1), new Pair<>(2, 4), new Pair<>(3, 2));
    LocalDate now = DateUtil.now();
    List<Pidor> pidors = new ArrayList<>();
    int totalVotes = 0;
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      pidors.add(getPidor(pidorIdsAndVote.getLeft()));
      doReturn(pidorIdsAndVote.getRight())
          .when(electionService)
          .getNumVotes(chatId, now, pidorIdsAndVote.getLeft());
      totalVotes += pidorIdsAndVote.getRight();
    }
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(totalVotes).when(electionService).getNumVotes(chatId, now);
    doReturn(pidors.size()).when(electionService).getNumToVote(chatId);

    // when
    printer.printInfo(chatId);

    // then

    checkActions(
        new BotTypeBotActionChecker(ChatActionBotAction.class),
        new ContainsTextBotActionChecker(
            new TextBuilder()
                .append(
                    new ParametizedText(
                        "Явка {0}%.", new IntText(100 * totalVotes / pidors.size())))
                .append(new NewLineText())
                .append(new NewLineText())
                .append(new ShortNameLinkedPidorText(getPidor(2)))
                .append(
                    new ParametizedText(
                        " - 4 голосов. Шанс стать пидором - {0}%",
                        new DoubleText(100 * (4 + 1) / ((double) totalVotes + pidors.size()))))
                .append(new NewLineText())
                .append(new NewLineText())
                .append(new ShortNameLinkedPidorText(getPidor(3)))
                .append(
                    new ParametizedText(
                        " - 2 голосов. Шанс стать пидором - {0}%",
                        new DoubleText(100 * (2 + 1) / ((double) totalVotes + pidors.size()))))
                .append(new NewLineText())
                .append(new NewLineText())
                .append(new ShortNameLinkedPidorText(getPidor(1)))
                .append(
                    new ParametizedText(
                        " - 1 голосов. Шанс стать пидором - {0}%",
                        new DoubleText(100 * (1 + 1) / ((double) totalVotes + pidors.size()))))
                .append(new NewLineText())
                .text()));
  }

  @Test
  public void printInfo_withoutCount_withZeros() {
    // given
    List<Pair<Integer, Integer>> pidorIdsAndVotes =
        Arrays.asList(new Pair<>(1, 0), new Pair<>(2, 4), new Pair<>(3, 2), new Pair<>(4, 0));
    LocalDate now = DateUtil.now();
    List<Pidor> pidors = new ArrayList<>();
    int totalVotes = 0;
    for (Pair<Integer, Integer> pidorIdsAndVote : pidorIdsAndVotes) {
      pidors.add(getPidor(pidorIdsAndVote.getLeft()));
      doReturn(pidorIdsAndVote.getRight())
          .when(electionService)
          .getNumVotes(chatId, now, pidorIdsAndVote.getLeft());
      totalVotes += pidorIdsAndVote.getRight();
    }
    doReturn(pidors).when(pidorService).getByChat(chatId);
    doReturn(totalVotes).when(electionService).getNumVotes(chatId, now);
    doReturn(pidors.size()).when(electionService).getNumToVote(chatId);

    // when
    printer.printInfo(chatId);

    // then
    checkActions(
        new BotTypeBotActionChecker(ChatActionBotAction.class),
        new ContainsTextBotActionChecker(
            new TextBuilder()
                .append(
                    new ParametizedText(
                        "Явка {0}%.", new IntText(100 * totalVotes / pidors.size())))
                .append(new NewLineText())
                .append(new NewLineText())
                .append(new ShortNameLinkedPidorText(getPidor(2)))
                .append(
                    new ParametizedText(
                        " - 4 голосов. Шанс стать пидором - {0}%",
                        new DoubleText(100 * (4 + 1) / ((double) totalVotes + pidors.size()))))
                .append(new NewLineText())
                .append(new NewLineText())
                .append(new ShortNameLinkedPidorText(getPidor(3)))
                .append(
                    new ParametizedText(
                        " - 2 голосов. Шанс стать пидором - {0}%",
                        new DoubleText(100 * (2 + 1) / ((double) totalVotes + pidors.size()))))
                .append(new NewLineText())
                .append(new NewLineText())
                .append(new SimpleText("У всех остальных"))
                .append(
                    new ParametizedText(
                        " - 0 голосов. Шанс стать пидором - {0}%",
                        new DoubleText(100 / ((double) totalVotes + pidors.size()))))
                .append(new NewLineText())
                .text()));
  }

  private Pidor getPidor(int id) {
    return new Pidor(id, chatId, "Pidor" + id);
  }
}
