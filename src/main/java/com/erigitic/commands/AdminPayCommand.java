package com.erigitic.commands;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.erigitic.config.AccountManager;
import com.erigitic.config.TEAccount;
import com.erigitic.main.TotalEconomy;

/**
 * Created by Erigitic on 9/7/2015.
 */
public class AdminPayCommand implements CommandExecutor
{
	private TotalEconomy totalEconomy;
	private AccountManager accountManager;
	private Currency defaultCurrency;

	public AdminPayCommand(TotalEconomy totalEconomy)
	{
		this.totalEconomy = totalEconomy;
		this.accountManager = totalEconomy.getAccountManager();
		this.defaultCurrency = accountManager.getDefaultCurrency();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
	{
		BigDecimal amount = new BigDecimal(args.<Integer> getOne("amount").get()).setScale(2, BigDecimal.ROUND_DOWN);
		Player recipient = args.<Player> getOne("player").get();

		if (amount.intValue() > 0)
		{
			TEAccount recipientAccount = (TEAccount) accountManager.getOrCreateAccount(recipient.getUniqueId()).get();
			TransactionResult transactionResult = recipientAccount.deposit(accountManager.getDefaultCurrency(), amount, Cause.of(NamedCause.of("TotalEconomy", this)));

			// TODO: Check for ResultType.FAILED?
			if (transactionResult.getResult() == ResultType.SUCCESS)
			{
				src.sendMessage(Text.of(TextColors.GRAY, "You have sent ", TextColors.GOLD, defaultCurrency.getSymbol(), amount, TextColors.GRAY, " to ", TextColors.GOLD, recipient.getName()));
				recipient.sendMessage(Text.of(TextColors.GRAY, "You have received ", TextColors.GOLD, defaultCurrency.getSymbol(), amount, TextColors.GRAY, " from ", TextColors.GOLD, src.getName(), "."));
			}
		}
		else
		{
			src.sendMessage(Text.of(TextColors.RED, "The amount must be positive."));
		}

		return CommandResult.success();
	}
}
