package org.codenova.moneylog.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.codenova.moneylog.entity.Verifications;

@Mapper
public interface VerificationRepository {

    public int create(Verifications verifications);
    public Verifications selectVerification(@Param("token") String token);
}
