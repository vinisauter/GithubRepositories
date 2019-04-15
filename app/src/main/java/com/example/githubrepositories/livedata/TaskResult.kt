@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.githubrepositories.livedata

/**
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */
class TaskResult<F>
/**
 * Constructor for a TaskResult.
 *
 * @param value the result object in the TaskResult
 * @param error the error object in the TaskResult
 */
    (val value: F?, val error: Throwable?) {

    /**
     * Checks the two objects for equality by delegating to their respective
     * [Object.equals] methods.
     *
     * @param other the [TaskResult] to which this one is to be checked for equality
     * @return true if the underlying objects of the TaskResult are both considered
     * equal
     */
    override fun equals(other: Any?): Boolean {
        if (other !is TaskResult<*>) {
            return false
        }
        val p = other as TaskResult<*>?
        return equals(p!!.value, value) && equals(p.error, error)
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the TaskResult
     */
    override fun hashCode(): Int {
        return (value?.hashCode() ?: 0) xor (error?.hashCode() ?: 0)
    }

    override fun toString(): String {
        return "TaskResult{" + value.toString() + " " + error.toString() + "}"
    }

    companion object {

        /**
         * Convenience method for creating an appropriately typed TaskResult.
         *
         * @param a the result object in the TaskResult
         * @param b the error object in the TaskResult
         * @return a TaskResult that is templatized with the types of a and b
         */
        fun <A> create(a: A, b: Throwable): TaskResult<A> {
            return TaskResult(a, b)
        }

        fun equals(a: Any?, b: Any?): Boolean {
            return a == b || a != null && a == b
        }
    }
}
