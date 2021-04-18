package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import io.izzel.taboolib.cronus.bridge.CronusBridge
import io.izzel.taboolib.cronus.bridge.database.IndexType
import io.izzel.taboolib.util.Coerce
import org.bukkit.entity.Player

/**
 * Raphael
 * ink.ptms.raphael.module.data.DatabaseMongoDB
 *
 * @author sky
 * @since 2021/3/23 3:04 下午
 */
class DatabaseMongoDB : Database() {

    val bridge = CronusBridge.get(
        Raphael.conf.getString("Database.source.MongoDB.client"),
        Raphael.conf.getString("Database.source.MongoDB.database"),
        Raphael.conf.getString("Database.source.MongoDB.collection"),
        IndexType.UUID
    )!!

    override fun getPermissions(player: Player): SerializedPermissions {
        val mapList = bridge.get(player.uniqueId.toString()).getMapList("Raphael.permissions")
        return SerializedPermissions(mapList.map {
            SerializedPermissions.Permission(it["permission"].toString(), Coerce.toLong(it["expired"]))
        }.filter { !it.isExpired })
    }

    override fun getVariables(player: Player): SerializedVariables {
        val mapList = bridge.get(player.uniqueId.toString()).getMapList("Raphael.variables")
        return SerializedVariables(mapList.map {
            SerializedVariables.Variable(it["variable"].toString(), it["data"].toString(), Coerce.toLong(it["expired"]))
        }.filter { !it.isExpired })
    }

    override fun getGroups(player: Player): SerializedGroups {
        val mapList = bridge.get(player.uniqueId.toString()).getMapList("Raphael.groups")
        return SerializedGroups(mapList.map {
            SerializedGroups.Group(it["group"].toString(), Coerce.toLong(it["expired"]))
        }.filter { !it.isExpired })
    }

    override fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean) {
        val data = bridge.get(player.uniqueId.toString())
        val mapList = data.getMapList("Raphael.permissions")
        mapList.removeIf {
            it["permission"] == permission.name
        }
        if (value) {
            mapList.add(mapOf("permission" to permission.name, "expired" to permission.expired))
        }
        data.set("Raphael.permissions", mapList)
    }

    override fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean) {
        val data = bridge.get(player.uniqueId.toString())
        val mapList = data.getMapList("Raphael.variables")
        mapList.removeIf {
            it["variable"] == variable.name
        }
        if (value) {
            mapList.add(mapOf("variable" to variable.name, "data" to variable.data, "expired" to variable.expired))
        }
        data.set("Raphael.variables", mapList)
    }

    override fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean) {
        val data = bridge.get(player.uniqueId.toString())
        val mapList = data.getMapList("Raphael.groups")
        mapList.removeIf {
            it["group"] == group.name
        }
        if (value) {
            mapList.add(mapOf("group" to group.name, "expired" to group.expired))
        }
        data.set("Raphael.groups", mapList)
    }
}