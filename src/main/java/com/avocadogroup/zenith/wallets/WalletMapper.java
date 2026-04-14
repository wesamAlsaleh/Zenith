package com.avocadogroup.zenith.wallets;

import com.avocadogroup.zenith.users.UserMapper;
import com.avocadogroup.zenith.wallets.dtos.WalletDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface WalletMapper {
    @Mapping(source = "user", target = "user")
    WalletDto toDto(Wallet wallet);

    Wallet toEntity(WalletDto walletDto);
}
