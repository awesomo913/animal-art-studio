package com.animalartstudio.server.service

import com.animalartstudio.server.db.ClientCrashReports
import com.animalartstudio.server.web.dto.ClientCrashAck
import com.animalartstudio.server.web.dto.ClientCrashIngest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class CrashIngestService {
  private val json =
      Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
      }

  fun store(body: ClientCrashIngest): ClientCrashAck =
      transaction {
        val merged = json.encodeToString(body)
        val id =
            ClientCrashReports.insert {
              it[ClientCrashReports.deviceId] = body.deviceId
              it[ClientCrashReports.appVersion] = body.appVersion
              it[ClientCrashReports.payloadJson] = merged
              it[ClientCrashReports.createdAt] = System.currentTimeMillis()
            } get ClientCrashReports.id
        ClientCrashAck(id = id, stored = true)
      }
}
