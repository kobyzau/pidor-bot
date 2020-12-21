package by.kobyzau.tg.bot.pbot.tasks;

import by.kobyzau.tg.bot.pbot.collectors.BotActionCollector;
import by.kobyzau.tg.bot.pbot.program.logger.Logger;
import by.kobyzau.tg.bot.pbot.program.text.IntText;
import by.kobyzau.tg.bot.pbot.program.text.ParametizedText;
import by.kobyzau.tg.bot.pbot.program.text.RandomText;
import by.kobyzau.tg.bot.pbot.program.text.SimpleText;
import by.kobyzau.tg.bot.pbot.service.BotService;
import by.kobyzau.tg.bot.pbot.service.ElectionService;
import by.kobyzau.tg.bot.pbot.service.TelegramService;
import by.kobyzau.tg.bot.pbot.tg.ChatAction;
import by.kobyzau.tg.bot.pbot.tg.action.PingMessageWrapperBotAction;
import by.kobyzau.tg.bot.pbot.tg.action.SendMessageBotAction;
import by.kobyzau.tg.bot.pbot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component("electionStartTask")
public class ElectionStartTask implements Task {

  @Autowired private ElectionService electionService;

  @Autowired private TelegramService telegramService;

  @Autowired private BotActionCollector botActionCollector;

  @Autowired private Logger logger;

  @Autowired private BotService botService;

  @Autowired
  @Qualifier("taskExecutor")
  private Executor executor;

  @Override
  public void processTask() {
    if (electionService.isElectionDay(DateUtil.now())) {
      logger.info("\uD83D\uDCC6 Task " + this.getClass().getSimpleName() + " is started");
      telegramService.getChatIds().stream()
          .filter(botService::isChatValid)
          .forEach(chatId -> executor.execute(() -> sendInfo(chatId)));
    }
  }

  private void sendInfo(long chatId) {
    try {
      botActionCollector.wait(chatId, ChatAction.TYPING);
      int numToVote = electionService.getNumToVote(chatId);
      botActionCollector.text(
          chatId, new RandomText("Всем здарова!", "Всем привет!", "Вечер в хату!"));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.add(
          new PingMessageWrapperBotAction(
              new SendMessageBotAction(
                  chatId, new SimpleText("Сегодня проходит великий День Пидор-Выборов!")),
              botService.canPinMessage(chatId)));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new RandomText(
              "Эти выборы самые честные",
              "Эти выборы самые справедливые",
              "Эти выборы самые демократичные",
              "Эти выборы будут проходить по мировым стандартам",
              "Эти выборы самые открытые"));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId, new SimpleText("Жми /pidor чтобы получить биллютень для голосования!"));
      botActionCollector.wait(chatId, ChatAction.TYPING);
      botActionCollector.text(
          chatId,
          new ParametizedText(
              "Но поспеши, ведь я принимаю голоса лишь первых {0}х", new IntText(numToVote)));
      botActionCollector.wait(chatId, ChatAction.TYPING);

    } catch (Exception e) {
      logger.error("Cannot start election game for chat " + chatId, e);
    }
  }
}
