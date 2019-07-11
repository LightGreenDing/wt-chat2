package com.chat.wtchat.dao;

import com.chat.wtchat.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 平台DAO
 *
 * @author Zed
 */
public interface PlatformDao extends JpaRepository<Platform, Integer> {
}
