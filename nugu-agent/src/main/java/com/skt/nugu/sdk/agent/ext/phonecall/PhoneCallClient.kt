/**
 * Copyright (c) 2020 SK Telecom Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skt.nugu.sdk.agent.ext.phonecall

import com.skt.nugu.sdk.agent.ext.phonecall.handler.*

interface PhoneCallClient
    : SendCandidatesDirectiveHandler.Controller
    , MakeCallDirectiveHandler.Controller
    , EndCallDirectiveHandler.Controller
    , AcceptCallDirectiveHandler.Controller
    , BlockIncomingCallDirectiveHandler.Controller
    , BlockNumberDirectiveHandler.Controller
{
    fun getContext(): Context

    interface OnStateChangeListener {
        fun onIdle(playServiceId: String)
        fun onOutgoing()
        fun onEstablished(playServiceId: String)
        fun onIncoming(playServiceId: String, caller: Caller)
    }

    fun addOnStateChangeListener(listener: OnStateChangeListener)
    fun removeOnStateChangeListener(listener: OnStateChangeListener)
}