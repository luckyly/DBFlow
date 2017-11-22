package com.raizlabs.android.dbflow.sql.language

import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.sql.Query
import com.raizlabs.android.dbflow.sql.QueryBuilder
import com.raizlabs.android.dbflow.sql.language.property.IProperty

/**
 * Description: Describes the method that the trigger uses.
 */
class TriggerMethod<TModel>
internal constructor(private val trigger: Trigger, private val methodName: String,
                     private var onTable: Class<TModel>, vararg properties: IProperty<*>) : Query {
    private var properties: List<IProperty<*>> = arrayListOf()
    private var forEachRow = false
    private var whenCondition: SQLOperator? = null

    override val query: String
        get() {
            val queryBuilder = QueryBuilder(trigger.query)
                    .append(methodName)
            if (properties.isNotEmpty()) {
                queryBuilder.appendSpaceSeparated("OF")
                        .appendArray(properties.toTypedArray())
            }
            queryBuilder.appendSpaceSeparated("ON").append(FlowManager.getTableName(onTable))

            if (forEachRow) {
                queryBuilder.appendSpaceSeparated("FOR EACH ROW")
            }

            if (whenCondition != null) {
                queryBuilder.append(" WHEN ")
                whenCondition!!.appendConditionToQuery(queryBuilder)
                queryBuilder.appendSpace()
            }

            queryBuilder.appendSpace()

            return queryBuilder.query
        }

    init {
        if (properties.isNotEmpty() && properties.getOrNull(0) != null) {
            if (methodName != UPDATE) {
                throw IllegalArgumentException("An Trigger OF can only be used with an UPDATE method")
            }
            this.properties = properties.toList()
        }
    }

    fun forEachRow() = apply {
        forEachRow = true
    }

    /**
     * Appends a WHEN condition after the ON name and before BEGIN...END
     *
     * @param condition The condition for the trigger
     * @return
     */
    @JvmName("when")
    fun whenever(condition: SQLOperator) = apply {
        whenCondition = condition
    }

    /**
     * Specify the logic that gets executed for this trigger. Supported statements include:
     * [Update], INSERT, [Delete],
     * and [Select]
     *
     * @param triggerLogicQuery The query to run for the BEGIN..END of the trigger
     * @return This trigger
     */
    fun begin(triggerLogicQuery: Query): CompletedTrigger<TModel> =
            CompletedTrigger(this, triggerLogicQuery)

    companion object {

        val DELETE = "DELETE"
        val INSERT = "INSERT"
        val UPDATE = "UPDATE"
    }
}
