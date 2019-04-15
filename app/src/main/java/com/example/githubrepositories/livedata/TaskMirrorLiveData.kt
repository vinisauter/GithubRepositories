@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.example.githubrepositories.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * TaskMirrorLiveData é responsavel por armazenar as informações (nome do método + parametros) das chamadas feitas a interface [T].
 *
 * @param <T> Tipo da interface
</T> */
class TaskMirrorLiveData<T> private constructor(clazz: Class<T>, callName: String) :
    LiveData<TaskMirrorLiveData.ReflexCall<T>>() {

    private val mirror: T

    /**
     * ReflexCall é responsavel por armazenar o informações sobre o nome do método e arqumentos das chamadas feitas a interface mirror [T].
     *
     * @param <T> Tipo da interface
    </T> */
    class ReflexCall<T> constructor(val callName: String, val method: Method, val args: Array<Any>) {

        /**
         * mapResultOnDelegate é responsavel por fazer o mapeamento da chama feita ao mirror `TaskMirrorLiveData.setTask()`
         * encontrar e executar o metodo na classe que implementa o {@param delegate}.
         *
         * @param delegate implementação do contrato de estados.
         */
        fun mapResultOnDelegate(delegate: T): Any? {
            print("On method invoked: " + callName + "->" + method.name)
            var result: Any? = null
            try {
                result = method.invoke(delegate, *args)
                println(" SUCCESS")
            } catch (e: Throwable) {
                println(" ERROR")
                e.printStackTrace()
            }

            return result
        }

        override fun toString(): String {
            return String.format("%s->%s", callName, method.name)
        }
    }

    fun observe(owner: LifecycleOwner, delegate: T) {
        super.observe(owner, Observer { tReflexCall -> tReflexCall.mapResultOnDelegate(delegate) })
    }

    fun observeForever(delegate: T) {
        super.observeForever { tReflexCall -> tReflexCall.mapResultOnDelegate(delegate) }
    }

    init {
        @Suppress("UNCHECKED_CAST")
        this.mirror = Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz)) { _, method, args ->
            postValue(ReflexCall(callName, method, args))
            null
        } as T
    }

    fun setTask(): T {
        return mirror
    }

    companion object {

        /**
         * @param callName       nome da tarefa a ser executada
         * @param interfaceToMap interface [T] para criar instancia de mapeamento [ReflexCall] do LiveData.
         * @return Cria um [LiveData] do tipo [ReflexCall]
         */
        fun <T> create(callName: String, interfaceToMap: Class<T>): TaskMirrorLiveData<T> {
            return TaskMirrorLiveData(interfaceToMap, callName)
        }
    }
}