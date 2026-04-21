package com.avocadogroup.zenith.transactions;

import com.avocadogroup.zenith.transactions.dtos.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "senderWallet.walletName", target = "senderWalletName")
    @Mapping(source = "receiverWallet.walletName", target = "receiverWalletName")
    @Mapping(source = "transactionType", target = "type")
    TransactionDto toDto(Transaction transaction);
}
