package com.jing.ddys.repository

import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.pow

private fun encode(jsonStr: String, callback: (Int) -> Char): String {
    val resultArray = StringBuilder()
    val E = hashMapOf<String, Int>();
    val F = hashMapOf<String, Boolean>();
    var G = "";
    var H = 2;
    var I = 3;
    var J = 2;
    var L = 0;
    var M = 0;
    var N = 0;
    var P = "";
    var ch: Char;
    var D = 0;
    var Q = 0;
    while (N < jsonStr.length) {
        ch = jsonStr[N]
        if (!E.containsKey(ch.toString())) {
            E[ch.toString()] = I++
            F[ch.toString()] = true
        }
        P = G + ch
        if (E.containsKey(P)) {
            G = P
        } else {
            if (F.containsKey(G)) {
                if (G[0].code < 256) {
                    D = 0
                    while (D < J) {
                        L = L.shl(1)
                        if (M == 5) {
                            M = 0
                            resultArray.append(callback(L))
                            L = 0
                        } else {
                            M++
                        }
                        D++
                    }
                    Q = G[0].code
                    D = 0
                    while (D < 8) {
                        L = Q.and(1).or(L.shl(1))
                        if (M == 5) {
                            M = 0
                            resultArray.append(callback(L))
                            L = 0
                        } else {
                            M++
                        }
                        Q = Q shr 1
                        D++
                    }
                } else {
                    Q = 1
                    D = 0
                    while (D < J) {
                        L = Q or (L shl 1)
                        if (M == 5) {
                            M = 0
                            resultArray.append(callback(L))
                            L = 0
                        } else {
                            M++
                        }
                        D++
                    }
                    Q = G[0].code
                    D = 0
                    while (D < 16) {
                        L = (Q and 1) or (L shl 1)
                        if (M == 5) {
                            M = 0
                            resultArray.append(callback(L))
                            L = 0
                        } else {
                            M++
                        }
                        Q = Q shr 1
                        D++
                    }
                }
                H--
                if (H == 0) {
                    H = 2.0.pow(J.toDouble()).toInt()
                    J++
                }
                F.remove(G)
            } else {
                Q = E[G] ?: 0
                D = 0
                while (D < J) {
                    L = (L shl 1) or (Q and 1)
                    if (M == 5) {
                        M = 0
                        resultArray.append(callback(L))
                        L = 0
                    } else {
                        M++
                    }
                    Q = Q shr 1
                    D++
                }
            }
            H--
            if (H == 0) {
                H = 2.0.pow(J).toInt()
                J++
            }
            E[P] = I++
            G = ch.toString()
        }
        N++
    }

    if (G.isNotEmpty()) {
        if (F.containsKey(G)) {
            if (G[0].code < 256) {
                D = 0
                while (D < J) {
                    L = L shl 1
                    if (M == 5) {
                        M = 0
                        resultArray.append(callback(L))
                        L = 0
                    } else {
                        M++
                    }
                    D++
                }
                Q = G[0].code
                D = 0
                while (D < 8) {
                    L = (Q and 1) or (L shl 1)
                    if (M == 5) {
                        M = 0
                        resultArray.append(callback(L))
                        L = 0
                    } else {
                        M++
                    }
                    Q = Q shr 1
                    D++
                }
            } else {
                Q = 1
                D = 0
                while (D < J) {
                    L = L shl 1 or Q
                    if (M == 5) {
                        M = 0
                        resultArray.append(callback(L))
                        L = 0
                    } else {
                        M++
                    }
                    D++
                }
                Q = G[0].code
                D = 0
                while (D < 16) {
                    L = (L shl 1) or (Q and 1)
                    if (M == 5) {
                        M = 0
                        resultArray.append(callback(L))
                        L = 0
                    } else {
                        M++
                    }
                    Q = Q shr 1
                    D++
                }
            }
            H--
            if (H == 0) {
                H = 2.0.pow(J).toInt()
                J++
            }
            F.remove(G)
        } else {
            Q = E[G] ?: 0
            D = 0
            while (D < J) {
                L = Q and 1 or (L shl 1)
                if (M == 5) {
                    M = 0
                    resultArray.append(callback(L))
                    L = 0
                } else {
                    M++
                }
                Q = Q shr 1
                D++
            }
        }
        H--
        if (H == 0) {
            J++
        }
    }
    Q = 2
    D = 0
    while (D < J) {
        L = Q and 1 or (L shl 1)
        if (M == 5) {
            M = 0
            resultArray.append(callback(L))
            L = 0
        } else {
            M++
        }
        Q = Q shr 1
        D++
    }
    while (true) {
        L = L shl 1
        if (M == 5) {
            resultArray.append(callback(L))
            break
        }
        M++
    }
    return resultArray.toString()
}

fun encodeParam(): String {
    val time = SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Date())
    val text =
        "{\"0\":[\"length\",\"innerWidth\",\"innerHeight\",\"scrollX\",\"pageXOffset\",\"scrollY\",\"pageYOffset\",\"screenX\",\"screenY\",\"screenLeft\",\"screenTop\",\"TEMPORARY\",\"n.maxTouchPoints\"],\"1\":[\"PERSISTENT\",\"d.childElementCount\",\"d.ELEMENT_NODE\",\"d.DOCUMENT_POSITION_DISCONNECTED\"],\"2\":[\"devicePixelRatio\",\"d.ATTRIBUTE_NODE\",\"d.DOCUMENT_POSITION_PRECEDING\"],\"3\":[\"d.TEXT_NODE\"],\"4\":[\"d.CDATA_SECTION_NODE\",\"d.DOCUMENT_POSITION_FOLLOWING\"],\"5\":[\"d.ENTITY_REFERENCE_NODE\"],\"6\":[\"d.ENTITY_NODE\"],\"7\":[\"d.PROCESSING_INSTRUCTION_NODE\"],\"8\":[\"n.hardwareConcurrency\",\"n.deviceMemory\",\"d.COMMENT_NODE\",\"d.DOCUMENT_POSITION_CONTAINS\"],\"9\":[\"d.nodeType\",\"d.DOCUMENT_NODE\"],\"10\":[\"d.DOCUMENT_TYPE_NODE\"],\"11\":[\"d.DOCUMENT_FRAGMENT_NODE\"],\"12\":[\"d.NOTATION_NODE\"],\"16\":[\"d.DOCUMENT_POSITION_CONTAINED_BY\"],\"32\":[\"d.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC\"],\"900\":[\"outerHeight\"],\"1440\":[\"outerWidth\"],\"o\":[\"window\",\"self\",\"document\",\"location\",\"customElements\",\"history\",\"navigation\",\"locationbar\",\"menubar\",\"personalbar\",\"scrollbars\",\"statusbar\",\"toolbar\",\"frames\",\"top\",\"parent\",\"frameElement\",\"navigator\",\"external\",\"screen\",\"visualViewport\",\"clientInformation\",\"styleMedia\",\"trustedTypes\",\"performance\",\"crypto\",\"indexedDB\",\"sessionStorage\",\"localStorage\",\"scheduler\",\"chrome\",\"caches\",\"cookieStore\",\"launchQueue\",\"speechSynthesis\",\"globalThis\",\"JSON\",\"Math\",\"Intl\",\"Atomics\",\"Reflect\",\"console\",\"CSS\",\"WebAssembly\",\"GPUBufferUsage\",\"GPUColorWrite\",\"GPUMapMode\",\"GPUShaderStage\",\"GPUTextureUsage\",\"n.scheduling\",\"n.userActivation\",\"n.geolocation\",\"n.connection\",\"n.plugins\",\"n.mimeTypes\",\"n.webkitTemporaryStorage\",\"n.webkitPersistentStorage\",\"n.bluetooth\",\"n.clipboard\",\"n.credentials\",\"n.keyboard\",\"n.managed\",\"n.mediaDevices\",\"n.storage\",\"n.serviceWorker\",\"n.virtualKeyboard\",\"n.wakeLock\",\"n.ink\",\"n.hid\",\"n.locks\",\"n.mediaCapabilities\",\"n.mediaSession\",\"n.permissions\",\"n.presentation\",\"n.serial\",\"n.gpu\",\"n.usb\",\"n.windowControlsOverlay\",\"n.xr\",\"n.userAgentData\",\"d.location\",\"d.implementation\",\"d.documentElement\",\"d.body\",\"d.head\",\"d.images\",\"d.embeds\",\"d.plugins\",\"d.links\",\"d.forms\",\"d.scripts\",\"d.defaultView\",\"d.anchors\",\"d.applets\",\"d.scrollingElement\",\"d.featurePolicy\",\"d.children\",\"d.firstElementChild\",\"d.lastElementChild\",\"d.activeElement\",\"d.styleSheets\",\"d.fonts\",\"d.fragmentDirective\",\"d.timeline\",\"d.childNodes\",\"d.firstChild\",\"d.lastChild\"],\"false\":[\"closed\",\"crossOriginIsolated\",\"credentialless\",\"originAgentCluster\",\"n.webdriver\",\"d.xmlStandalone\",\"d.hidden\",\"d.wasDiscarded\",\"d.prerendering\",\"d.webkitHidden\",\"d.fullscreen\",\"d.webkitIsFullScreen\"],\"x\":[\"opener\",\"onsearch\",\"onappinstalled\",\"onbeforeinstallprompt\",\"onbeforexrselect\",\"onabort\",\"onbeforeinput\",\"onblur\",\"oncancel\",\"oncanplay\",\"oncanplaythrough\",\"onchange\",\"onclick\",\"onclose\",\"oncontextlost\",\"oncontextmenu\",\"oncontextrestored\",\"oncuechange\",\"ondblclick\",\"ondrag\",\"ondragend\",\"ondragenter\",\"ondragleave\",\"ondragover\",\"ondragstart\",\"ondrop\",\"ondurationchange\",\"onemptied\",\"onended\",\"onerror\",\"onfocus\",\"onformdata\",\"oninput\",\"oninvalid\",\"onkeydown\",\"onkeypress\",\"onkeyup\",\"onload\",\"onloadeddata\",\"onloadedmetadata\",\"onloadstart\",\"onmousedown\",\"onmouseenter\",\"onmouseleave\",\"onmousemove\",\"onmouseout\",\"onmouseover\",\"onmouseup\",\"onmousewheel\",\"onpause\",\"onplay\",\"onplaying\",\"onprogress\",\"onratechange\",\"onreset\",\"onresize\",\"onscroll\",\"onsecuritypolicyviolation\",\"onseeked\",\"onseeking\",\"onselect\",\"onslotchange\",\"onstalled\",\"onsubmit\",\"onsuspend\",\"ontimeupdate\",\"ontoggle\",\"onvolumechange\",\"onwaiting\",\"onwebkitanimationend\",\"onwebkitanimationiteration\",\"onwebkitanimationstart\",\"onwebkittransitionend\",\"onwheel\",\"onauxclick\",\"ongotpointercapture\",\"onlostpointercapture\",\"onpointerdown\",\"onpointermove\",\"onpointerrawupdate\",\"onpointerup\",\"onpointercancel\",\"onpointerover\",\"onpointerout\",\"onpointerenter\",\"onpointerleave\",\"onselectstart\",\"onselectionchange\",\"onanimationend\",\"onanimationiteration\",\"onanimationstart\",\"ontransitionrun\",\"ontransitionstart\",\"ontransitionend\",\"ontransitioncancel\",\"onafterprint\",\"onbeforeprint\",\"onbeforeunload\",\"onhashchange\",\"onlanguagechange\",\"onmessage\",\"onmessageerror\",\"onoffline\",\"ononline\",\"onpagehide\",\"onpageshow\",\"onpopstate\",\"onrejectionhandled\",\"onstorage\",\"onunhandledrejection\",\"onunload\",\"ondevicemotion\",\"ondeviceorientation\",\"ondeviceorientationabsolute\",\"onbeforematch\",\"onbeforetoggle\",\"oncontentvisibilityautostatechange\",\"onscrollend\",\"n.doNotTrack\",\"d.doctype\",\"d.xmlEncoding\",\"d.xmlVersion\",\"d.currentScript\",\"d.onreadystatechange\",\"d.all\",\"d.onpointerlockchange\",\"d.onpointerlockerror\",\"d.onbeforecopy\",\"d.onbeforecut\",\"d.onbeforepaste\",\"d.onfreeze\",\"d.onprerenderingchange\",\"d.onresume\",\"d.onsearch\",\"d.onvisibilitychange\",\"d.onfullscreenchange\",\"d.onfullscreenerror\",\"d.webkitCurrentFullScreenElement\",\"d.webkitFullscreenElement\",\"d.onwebkitfullscreenchange\",\"d.onwebkitfullscreenerror\",\"d.rootElement\",\"d.pictureInPictureElement\",\"d.onbeforexrselect\",\"d.onabort\",\"d.onbeforeinput\",\"d.onblur\",\"d.oncancel\",\"d.oncanplay\",\"d.oncanplaythrough\",\"d.onchange\",\"d.onclick\",\"d.onclose\",\"d.oncontextlost\",\"d.oncontextmenu\",\"d.oncontextrestored\",\"d.oncuechange\",\"d.ondblclick\",\"d.ondrag\",\"d.ondragend\",\"d.ondragenter\",\"d.ondragleave\",\"d.ondragover\",\"d.ondragstart\",\"d.ondrop\",\"d.ondurationchange\",\"d.onemptied\",\"d.onended\",\"d.onerror\",\"d.onfocus\",\"d.onformdata\",\"d.oninput\",\"d.oninvalid\",\"d.onkeydown\",\"d.onkeypress\",\"d.onkeyup\",\"d.onload\",\"d.onloadeddata\",\"d.onloadedmetadata\",\"d.onloadstart\",\"d.onmousedown\",\"d.onmouseenter\",\"d.onmouseleave\",\"d.onmousemove\",\"d.onmouseout\",\"d.onmouseover\",\"d.onmouseup\",\"d.onmousewheel\",\"d.onpause\",\"d.onplay\",\"d.onplaying\",\"d.onprogress\",\"d.onratechange\",\"d.onreset\",\"d.onresize\",\"d.onscroll\",\"d.onsecuritypolicyviolation\",\"d.onseeked\",\"d.onseeking\",\"d.onselect\",\"d.onslotchange\",\"d.onstalled\",\"d.onsubmit\",\"d.onsuspend\",\"d.ontimeupdate\",\"d.ontoggle\",\"d.onvolumechange\",\"d.onwaiting\",\"d.onwebkitanimationend\",\"d.onwebkitanimationiteration\",\"d.onwebkitanimationstart\",\"d.onwebkittransitionend\",\"d.onwheel\",\"d.onauxclick\",\"d.ongotpointercapture\",\"d.onlostpointercapture\",\"d.onpointerdown\",\"d.onpointermove\",\"d.onpointerrawupdate\",\"d.onpointerup\",\"d.onpointercancel\",\"d.onpointerover\",\"d.onpointerout\",\"d.onpointerenter\",\"d.onpointerleave\",\"d.onselectstart\",\"d.onselectionchange\",\"d.onanimationend\",\"d.onanimationiteration\",\"d.onanimationstart\",\"d.ontransitionrun\",\"d.ontransitionstart\",\"d.ontransitionend\",\"d.ontransitioncancel\",\"d.oncopy\",\"d.oncut\",\"d.onpaste\",\"d.pointerLockElement\",\"d.fullscreenElement\",\"d.onbeforematch\",\"d.onbeforetoggle\",\"d.oncontentvisibilityautostatechange\",\"d.onscrollend\",\"d.ownerDocument\",\"d.parentNode\",\"d.parentElement\",\"d.previousSibling\",\"d.nextSibling\",\"d.nodeValue\",\"d.textContent\"],\"https://ddys.art\":[\"origin\"],\"true\":[\"isSecureContext\",\"offscreenBuffering\",\"n.pdfViewerEnabled\",\"n.cookieEnabled\",\"n.onLine\",\"d.fullscreenEnabled\",\"d.webkitFullscreenEnabled\",\"d.pictureInPictureEnabled\",\"d.isConnected\"],\"N\":[\"alert\",\"atob\",\"blur\",\"btoa\",\"cancelAnimationFrame\",\"cancelIdleCallback\",\"captureEvents\",\"clearInterval\",\"clearTimeout\",\"close\",\"confirm\",\"createImageBitmap\",\"fetch\",\"find\",\"focus\",\"getComputedStyle\",\"getSelection\",\"matchMedia\",\"moveBy\",\"moveTo\",\"open\",\"postMessage\",\"print\",\"prompt\",\"queueMicrotask\",\"releaseEvents\",\"reportError\",\"requestAnimationFrame\",\"requestIdleCallback\",\"resizeBy\",\"resizeTo\",\"scroll\",\"scrollBy\",\"scrollTo\",\"setInterval\",\"setTimeout\",\"stop\",\"structuredClone\",\"webkitCancelAnimationFrame\",\"webkitRequestAnimationFrame\",\"getScreenDetails\",\"queryLocalFonts\",\"showDirectoryPicker\",\"showOpenFilePicker\",\"showSaveFilePicker\",\"openDatabase\",\"webkitRequestFileSystem\",\"webkitResolveLocalFileSystemURL\",\"addEventListener\",\"dispatchEvent\",\"removeEventListener\",\"Object\",\"Function\",\"Number\",\"parseFloat\",\"parseInt\",\"Boolean\",\"String\",\"Symbol\",\"Date\",\"Promise\",\"RegExp\",\"Error\",\"AggregateError\",\"EvalError\",\"RangeError\",\"ReferenceError\",\"SyntaxError\",\"TypeError\",\"URIError\",\"ArrayBuffer\",\"Uint8Array\",\"Int8Array\",\"Uint16Array\",\"Int16Array\",\"Uint32Array\",\"Int32Array\",\"Float32Array\",\"Float64Array\",\"Uint8ClampedArray\",\"BigUint64Array\",\"BigInt64Array\",\"DataView\",\"Map\",\"BigInt\",\"Set\",\"WeakMap\",\"WeakSet\",\"Proxy\",\"FinalizationRegistry\",\"WeakRef\",\"decodeURI\",\"decodeURIComponent\",\"encodeURI\",\"encodeURIComponent\",\"escape\",\"unescape\",\"eval\",\"isFinite\",\"isNaN\",\"Option\",\"Image\",\"Audio\",\"webkitURL\",\"webkitRTCPeerConnection\",\"webkitMediaStream\",\"WebKitMutationObserver\",\"WebKitCSSMatrix\",\"XSLTProcessor\",\"XPathResult\",\"XPathExpression\",\"XPathEvaluator\",\"XMLSerializer\",\"XMLHttpRequestUpload\",\"XMLHttpRequestEventTarget\",\"XMLHttpRequest\",\"XMLDocument\",\"WritableStreamDefaultWriter\",\"WritableStreamDefaultController\",\"WritableStream\",\"Worker\",\"Window\",\"WheelEvent\",\"WebSocket\",\"WebGLVertexArrayObject\",\"WebGLUniformLocation\",\"WebGLTransformFeedback\",\"WebGLTexture\",\"WebGLSync\",\"WebGLShaderPrecisionFormat\",\"WebGLShader\",\"WebGLSampler\",\"WebGLRenderingContext\",\"WebGLRenderbuffer\",\"WebGLQuery\",\"WebGLProgram\",\"WebGLFramebuffer\",\"WebGLContextEvent\",\"WebGLBuffer\",\"WebGLActiveInfo\",\"WebGL2RenderingContext\",\"WaveShaperNode\",\"VisualViewport\",\"VirtualKeyboardGeometryChangeEvent\",\"ValidityState\",\"VTTCue\",\"UserActivation\",\"URLSearchParams\",\"URLPattern\",\"URL\",\"UIEvent\",\"TrustedTypePolicyFactory\",\"TrustedTypePolicy\",\"TrustedScriptURL\",\"TrustedScript\",\"TrustedHTML\",\"TreeWalker\",\"TransitionEvent\",\"TransformStreamDefaultController\",\"TransformStream\",\"TrackEvent\",\"TouchList\",\"TouchEvent\",\"Touch\",\"TimeRanges\",\"TextTrackList\",\"TextTrackCueList\",\"TextTrackCue\",\"TextTrack\",\"TextMetrics\",\"TextEvent\",\"TextEncoderStream\",\"TextEncoder\",\"TextDecoderStream\",\"TextDecoder\",\"Text\",\"TaskSignal\",\"TaskPriorityChangeEvent\",\"TaskController\",\"TaskAttributionTiming\",\"SyncManager\",\"SubmitEvent\",\"StyleSheetList\",\"StyleSheet\",\"StylePropertyMapReadOnly\",\"StylePropertyMap\",\"StorageEvent\",\"Storage\",\"StereoPannerNode\",\"StaticRange\",\"SourceBufferList\",\"SourceBuffer\",\"ShadowRoot\",\"Selection\",\"SecurityPolicyViolationEvent\",\"ScriptProcessorNode\",\"ScreenOrientation\",\"Screen\",\"Scheduling\",\"Scheduler\",\"SVGViewElement\",\"SVGUseElement\",\"SVGUnitTypes\",\"SVGTransformList\",\"SVGTransform\",\"SVGTitleElement\",\"SVGTextPositioningElement\",\"SVGTextPathElement\",\"SVGTextElement\",\"SVGTextContentElement\",\"SVGTSpanElement\",\"SVGSymbolElement\",\"SVGSwitchElement\",\"SVGStyleElement\",\"SVGStringList\",\"SVGStopElement\",\"SVGSetElement\",\"SVGScriptElement\",\"SVGSVGElement\",\"SVGRectElement\",\"SVGRect\",\"SVGRadialGradientElement\",\"SVGPreserveAspectRatio\",\"SVGPolylineElement\",\"SVGPolygonElement\",\"SVGPointList\",\"SVGPoint\",\"SVGPatternElement\",\"SVGPathElement\",\"SVGNumberList\",\"SVGNumber\",\"SVGMetadataElement\",\"SVGMatrix\",\"SVGMaskElement\",\"SVGMarkerElement\",\"SVGMPathElement\",\"SVGLinearGradientElement\",\"SVGLineElement\",\"SVGLengthList\",\"SVGLength\",\"SVGImageElement\",\"SVGGraphicsElement\",\"SVGGradientElement\",\"SVGGeometryElement\",\"SVGGElement\",\"SVGForeignObjectElement\",\"SVGFilterElement\",\"SVGFETurbulenceElement\",\"SVGFETileElement\",\"SVGFESpotLightElement\",\"SVGFESpecularLightingElement\",\"SVGFEPointLightElement\",\"SVGFEOffsetElement\",\"SVGFEMorphologyElement\",\"SVGFEMergeNodeElement\",\"SVGFEMergeElement\",\"SVGFEImageElement\",\"SVGFEGaussianBlurElement\",\"SVGFEFuncRElement\",\"SVGFEFuncGElement\",\"SVGFEFuncBElement\",\"SVGFEFuncAElement\",\"SVGFEFloodElement\",\"SVGFEDropShadowElement\",\"SVGFEDistantLightElement\",\"SVGFEDisplacementMapElement\",\"SVGFEDiffuseLightingElement\",\"SVGFEConvolveMatrixElement\",\"SVGFECompositeElement\",\"SVGFEComponentTransferElement\",\"SVGFEColorMatrixElement\",\"SVGFEBlendElement\",\"SVGEllipseElement\",\"SVGElement\",\"SVGDescElement\",\"SVGDefsElement\",\"SVGComponentTransferFunctionElement\",\"SVGClipPathElement\",\"SVGCircleElement\",\"SVGAnimationElement\",\"SVGAnimatedTransformList\",\"SVGAnimatedString\",\"SVGAnimatedRect\",\"SVGAnimatedPreserveAspectRatio\",\"SVGAnimatedNumberList\",\"SVGAnimatedNumber\",\"SVGAnimatedLengthList\",\"SVGAnimatedLength\",\"SVGAnimatedInteger\",\"SVGAnimatedEnumeration\",\"SVGAnimatedBoolean\",\"SVGAnimatedAngle\",\"SVGAnimateTransformElement\",\"SVGAnimateMotionElement\",\"SVGAnimateElement\",\"SVGAngle\",\"SVGAElement\",\"Response\",\"ResizeObserverSize\",\"ResizeObserverEntry\",\"ResizeObserver\",\"Request\",\"ReportingObserver\",\"ReadableStreamDefaultReader\",\"ReadableStreamDefaultController\",\"ReadableStreamBYOBRequest\",\"ReadableStreamBYOBReader\",\"ReadableStream\",\"ReadableByteStreamController\",\"Range\",\"RadioNodeList\",\"RTCTrackEvent\",\"RTCStatsReport\",\"RTCSessionDescription\",\"RTCSctpTransport\",\"RTCRtpTransceiver\",\"RTCRtpSender\",\"RTCRtpReceiver\",\"RTCPeerConnectionIceEvent\",\"RTCPeerConnectionIceErrorEvent\",\"RTCPeerConnection\",\"RTCIceTransport\",\"RTCIceCandidate\",\"RTCErrorEvent\",\"RTCError\",\"RTCEncodedVideoFrame\",\"RTCEncodedAudioFrame\",\"RTCDtlsTransport\",\"RTCDataChannelEvent\",\"RTCDataChannel\",\"RTCDTMFToneChangeEvent\",\"RTCDTMFSender\",\"RTCCertificate\",\"PromiseRejectionEvent\",\"ProgressEvent\",\"Profiler\",\"ProcessingInstruction\",\"PopStateEvent\",\"PointerEvent\",\"PluginArray\",\"Plugin\",\"PictureInPictureWindow\",\"PictureInPictureEvent\",\"PeriodicWave\",\"PerformanceTiming\",\"PerformanceServerTiming\",\"PerformanceResourceTiming\",\"PerformancePaintTiming\",\"PerformanceObserverEntryList\",\"PerformanceObserver\",\"PerformanceNavigationTiming\",\"PerformanceNavigation\",\"PerformanceMeasure\",\"PerformanceMark\",\"PerformanceLongTaskTiming\",\"PerformanceEventTiming\",\"PerformanceEntry\",\"PerformanceElementTiming\",\"Performance\",\"Path2D\",\"PannerNode\",\"PageTransitionEvent\",\"OverconstrainedError\",\"OscillatorNode\",\"OffscreenCanvasRenderingContext2D\",\"OffscreenCanvas\",\"OfflineAudioContext\",\"OfflineAudioCompletionEvent\",\"NodeList\",\"NodeIterator\",\"NodeFilter\",\"Node\",\"NetworkInformation\",\"Navigator\",\"NavigationTransition\",\"NavigationHistoryEntry\",\"NavigationDestination\",\"NavigationCurrentEntryChangeEvent\",\"Navigation\",\"NavigateEvent\",\"NamedNodeMap\",\"MutationRecord\",\"MutationObserver\",\"MutationEvent\",\"MouseEvent\",\"MimeTypeArray\",\"MimeType\",\"MessagePort\",\"MessageEvent\",\"MessageChannel\",\"MediaStreamTrackProcessor\",\"MediaStreamTrackGenerator\",\"MediaStreamTrackEvent\",\"MediaStreamTrack\",\"MediaStreamEvent\",\"MediaStreamAudioSourceNode\",\"MediaStreamAudioDestinationNode\",\"MediaStream\",\"MediaSourceHandle\",\"MediaSource\",\"MediaRecorder\",\"MediaQueryListEvent\",\"MediaQueryList\",\"MediaList\",\"MediaError\",\"MediaEncryptedEvent\",\"MediaElementAudioSourceNode\",\"MediaCapabilities\",\"Location\",\"LayoutShiftAttribution\",\"LayoutShift\",\"LargestContentfulPaint\",\"KeyframeEffect\",\"KeyboardEvent\",\"IntersectionObserverEntry\",\"IntersectionObserver\",\"InputEvent\",\"InputDeviceInfo\",\"InputDeviceCapabilities\",\"ImageData\",\"ImageCapture\",\"ImageBitmapRenderingContext\",\"ImageBitmap\",\"IdleDeadline\",\"IIRFilterNode\",\"IDBVersionChangeEvent\",\"IDBTransaction\",\"IDBRequest\",\"IDBOpenDBRequest\",\"IDBObjectStore\",\"IDBKeyRange\",\"IDBIndex\",\"IDBFactory\",\"IDBDatabase\",\"IDBCursorWithValue\",\"IDBCursor\",\"History\",\"Headers\",\"HashChangeEvent\",\"HTMLVideoElement\",\"HTMLUnknownElement\",\"HTMLUListElement\",\"HTMLTrackElement\",\"HTMLTitleElement\",\"HTMLTimeElement\",\"HTMLTextAreaElement\",\"HTMLTemplateElement\",\"HTMLTableSectionElement\",\"HTMLTableRowElement\",\"HTMLTableElement\",\"HTMLTableColElement\",\"HTMLTableCellElement\",\"HTMLTableCaptionElement\",\"HTMLStyleElement\",\"HTMLSpanElement\",\"HTMLSourceElement\",\"HTMLSlotElement\",\"HTMLSelectElement\",\"HTMLScriptElement\",\"HTMLQuoteElement\",\"HTMLProgressElement\",\"HTMLPreElement\",\"HTMLPictureElement\",\"HTMLParamElement\",\"HTMLParagraphElement\",\"HTMLOutputElement\",\"HTMLOptionsCollection\",\"HTMLOptionElement\",\"HTMLOptGroupElement\",\"HTMLObjectElement\",\"HTMLOListElement\",\"HTMLModElement\",\"HTMLMeterElement\",\"HTMLMetaElement\",\"HTMLMenuElement\",\"HTMLMediaElement\",\"HTMLMarqueeElement\",\"HTMLMapElement\",\"HTMLLinkElement\",\"HTMLLegendElement\",\"HTMLLabelElement\",\"HTMLLIElement\",\"HTMLInputElement\",\"HTMLImageElement\",\"HTMLIFrameElement\",\"HTMLHtmlElement\",\"HTMLHeadingElement\",\"HTMLHeadElement\",\"HTMLHRElement\",\"HTMLFrameSetElement\",\"HTMLFrameElement\",\"HTMLFormElement\",\"HTMLFormControlsCollection\",\"HTMLFontElement\",\"HTMLFieldSetElement\",\"HTMLEmbedElement\",\"HTMLElement\",\"HTMLDocument\",\"HTMLDivElement\",\"HTMLDirectoryElement\",\"HTMLDialogElement\",\"HTMLDetailsElement\",\"HTMLDataListElement\",\"HTMLDataElement\",\"HTMLDListElement\",\"HTMLCollection\",\"HTMLCanvasElement\",\"HTMLButtonElement\",\"HTMLBodyElement\",\"HTMLBaseElement\",\"HTMLBRElement\",\"HTMLAudioElement\",\"HTMLAreaElement\",\"HTMLAnchorElement\",\"HTMLAllCollection\",\"GeolocationPositionError\",\"GeolocationPosition\",\"GeolocationCoordinates\",\"Geolocation\",\"GamepadHapticActuator\",\"GamepadEvent\",\"GamepadButton\",\"Gamepad\",\"GainNode\",\"FormDataEvent\",\"FormData\",\"FontFaceSetLoadEvent\",\"FontFace\",\"FocusEvent\",\"FileReader\",\"FileList\",\"File\",\"FeaturePolicy\",\"External\",\"EventTarget\",\"EventSource\",\"EventCounts\",\"Event\",\"ErrorEvent\",\"ElementInternals\",\"Element\",\"DynamicsCompressorNode\",\"DragEvent\",\"DocumentType\",\"DocumentFragment\",\"Document\",\"DelayNode\",\"DecompressionStream\",\"DataTransferItemList\",\"DataTransferItem\",\"DataTransfer\",\"DOMTokenList\",\"DOMStringMap\",\"DOMStringList\",\"DOMRectReadOnly\",\"DOMRectList\",\"DOMRect\",\"DOMQuad\",\"DOMPointReadOnly\",\"DOMPoint\",\"DOMParser\",\"DOMMatrixReadOnly\",\"DOMMatrix\",\"DOMImplementation\",\"DOMException\",\"DOMError\",\"CustomStateSet\",\"CustomEvent\",\"CustomElementRegistry\",\"Crypto\",\"CountQueuingStrategy\",\"ConvolverNode\",\"ConstantSourceNode\",\"CompressionStream\",\"CompositionEvent\",\"Comment\",\"CloseEvent\",\"ClipboardEvent\",\"CharacterData\",\"ChannelSplitterNode\",\"ChannelMergerNode\",\"CanvasRenderingContext2D\",\"CanvasPattern\",\"CanvasGradient\",\"CanvasCaptureMediaStreamTrack\",\"CSSVariableReferenceValue\",\"CSSUnparsedValue\",\"CSSUnitValue\",\"CSSTranslate\",\"CSSTransformValue\",\"CSSTransformComponent\",\"CSSSupportsRule\",\"CSSStyleValue\",\"CSSStyleSheet\",\"CSSStyleRule\",\"CSSStyleDeclaration\",\"CSSSkewY\",\"CSSSkewX\",\"CSSSkew\",\"CSSScale\",\"CSSRuleList\",\"CSSRule\",\"CSSRotate\",\"CSSPropertyRule\",\"CSSPositionValue\",\"CSSPerspective\",\"CSSPageRule\",\"CSSNumericValue\",\"CSSNumericArray\",\"CSSNamespaceRule\",\"CSSMediaRule\",\"CSSMatrixComponent\",\"CSSMathValue\",\"CSSMathSum\",\"CSSMathProduct\",\"CSSMathNegate\",\"CSSMathMin\",\"CSSMathMax\",\"CSSMathInvert\",\"CSSMathClamp\",\"CSSLayerStatementRule\",\"CSSLayerBlockRule\",\"CSSKeywordValue\",\"CSSKeyframesRule\",\"CSSKeyframeRule\",\"CSSImportRule\",\"CSSImageValue\",\"CSSGroupingRule\",\"CSSFontPaletteValuesRule\",\"CSSFontFaceRule\",\"CSSCounterStyleRule\",\"CSSContainerRule\",\"CSSConditionRule\",\"CDATASection\",\"ByteLengthQueuingStrategy\",\"BroadcastChannel\",\"BlobEvent\",\"Blob\",\"BiquadFilterNode\",\"BeforeUnloadEvent\",\"BeforeInstallPromptEvent\",\"BaseAudioContext\",\"BarProp\",\"AudioWorkletNode\",\"AudioSinkInfo\",\"AudioScheduledSourceNode\",\"AudioProcessingEvent\",\"AudioParamMap\",\"AudioParam\",\"AudioNode\",\"AudioListener\",\"AudioDestinationNode\",\"AudioContext\",\"AudioBufferSourceNode\",\"AudioBuffer\",\"Attr\",\"AnimationEvent\",\"AnimationEffect\",\"Animation\",\"AnalyserNode\",\"AbstractRange\",\"AbortSignal\",\"AbortController\",\"AbsoluteOrientationSensor\",\"Accelerometer\",\"AudioWorklet\",\"BatteryManager\",\"Cache\",\"CacheStorage\",\"Clipboard\",\"ClipboardItem\",\"CookieChangeEvent\",\"CookieStore\",\"CookieStoreManager\",\"Credential\",\"CredentialsContainer\",\"CryptoKey\",\"DeviceMotionEvent\",\"DeviceMotionEventAcceleration\",\"DeviceMotionEventRotationRate\",\"DeviceOrientationEvent\",\"FederatedCredential\",\"GravitySensor\",\"Gyroscope\",\"Keyboard\",\"KeyboardLayoutMap\",\"LinearAccelerationSensor\",\"Lock\",\"LockManager\",\"MIDIAccess\",\"MIDIConnectionEvent\",\"MIDIInput\",\"MIDIInputMap\",\"MIDIMessageEvent\",\"MIDIOutput\",\"MIDIOutputMap\",\"MIDIPort\",\"MediaDeviceInfo\",\"MediaDevices\",\"MediaKeyMessageEvent\",\"MediaKeySession\",\"MediaKeyStatusMap\",\"MediaKeySystemAccess\",\"MediaKeys\",\"NavigationPreloadManager\",\"NavigatorManagedData\",\"OrientationSensor\",\"PasswordCredential\",\"RelativeOrientationSensor\",\"Sanitizer\",\"ScreenDetailed\",\"ScreenDetails\",\"Sensor\",\"SensorErrorEvent\",\"ServiceWorker\",\"ServiceWorkerContainer\",\"ServiceWorkerRegistration\",\"StorageManager\",\"SubtleCrypto\",\"VirtualKeyboard\",\"WebTransport\",\"WebTransportBidirectionalStream\",\"WebTransportDatagramDuplexStream\",\"WebTransportError\",\"Worklet\",\"XRDOMOverlayState\",\"XRLayer\",\"XRWebGLBinding\",\"AudioData\",\"EncodedAudioChunk\",\"EncodedVideoChunk\",\"ImageTrack\",\"ImageTrackList\",\"VideoColorSpace\",\"VideoFrame\",\"AudioDecoder\",\"AudioEncoder\",\"ImageDecoder\",\"VideoDecoder\",\"VideoEncoder\",\"AuthenticatorAssertionResponse\",\"AuthenticatorAttestationResponse\",\"AuthenticatorResponse\",\"PublicKeyCredential\",\"BarcodeDetector\",\"Bluetooth\",\"BluetoothCharacteristicProperties\",\"BluetoothDevice\",\"BluetoothRemoteGATTCharacteristic\",\"BluetoothRemoteGATTDescriptor\",\"BluetoothRemoteGATTServer\",\"BluetoothRemoteGATTService\",\"CaptureController\",\"EyeDropper\",\"FileSystemDirectoryHandle\",\"FileSystemFileHandle\",\"FileSystemHandle\",\"FileSystemWritableFileStream\",\"FontData\",\"FragmentDirective\",\"GPU\",\"GPUAdapter\",\"GPUAdapterInfo\",\"GPUBindGroup\",\"GPUBindGroupLayout\",\"GPUBuffer\",\"GPUCanvasContext\",\"GPUCommandBuffer\",\"GPUCommandEncoder\",\"GPUCompilationInfo\",\"GPUCompilationMessage\",\"GPUComputePassEncoder\",\"GPUComputePipeline\",\"GPUDevice\",\"GPUDeviceLostInfo\",\"GPUError\",\"GPUExternalTexture\",\"GPUInternalError\",\"GPUOutOfMemoryError\",\"GPUPipelineError\",\"GPUPipelineLayout\",\"GPUQuerySet\",\"GPUQueue\",\"GPURenderBundle\",\"GPURenderBundleEncoder\",\"GPURenderPassEncoder\",\"GPURenderPipeline\",\"GPUSampler\",\"GPUShaderModule\",\"GPUSupportedFeatures\",\"GPUSupportedLimits\",\"GPUTexture\",\"GPUTextureView\",\"GPUUncapturedErrorEvent\",\"GPUValidationError\",\"HID\",\"HIDConnectionEvent\",\"HIDDevice\",\"HIDInputReportEvent\",\"IdentityCredential\",\"IdleDetector\",\"LaunchParams\",\"LaunchQueue\",\"OTPCredential\",\"PaymentAddress\",\"PaymentRequest\",\"PaymentResponse\",\"PaymentMethodChangeEvent\",\"Presentation\",\"PresentationAvailability\",\"PresentationConnection\",\"PresentationConnectionAvailableEvent\",\"PresentationConnectionCloseEvent\",\"PresentationConnectionList\",\"PresentationReceiver\",\"PresentationRequest\",\"Serial\",\"SerialPort\",\"ToggleEvent\",\"USB\",\"USBAlternateInterface\",\"USBConfiguration\",\"USBConnectionEvent\",\"USBDevice\",\"USBEndpoint\",\"USBInTransferResult\",\"USBInterface\",\"USBIsochronousInTransferPacket\",\"USBIsochronousInTransferResult\",\"USBIsochronousOutTransferPacket\",\"USBIsochronousOutTransferResult\",\"USBOutTransferResult\",\"WakeLock\",\"WakeLockSentinel\",\"WindowControlsOverlay\",\"WindowControlsOverlayGeometryChangeEvent\",\"XRAnchor\",\"XRAnchorSet\",\"XRBoundedReferenceSpace\",\"XRCPUDepthInformation\",\"XRCamera\",\"XRDepthInformation\",\"XRFrame\",\"XRHitTestResult\",\"XRHitTestSource\",\"XRInputSource\",\"XRInputSourceArray\",\"XRInputSourceEvent\",\"XRInputSourcesChangeEvent\",\"XRLightEstimate\",\"XRLightProbe\",\"XRPose\",\"XRRay\",\"XRReferenceSpace\",\"XRReferenceSpaceEvent\",\"XRRenderState\",\"XRRigidTransform\",\"XRSession\",\"XRSessionEvent\",\"XRSpace\",\"XRSystem\",\"XRTransientInputHitTestResult\",\"XRTransientInputHitTestSource\",\"XRView\",\"XRViewerPose\",\"XRViewport\",\"XRWebGLDepthInformation\",\"XRWebGLLayer\",\"AnimationPlaybackEvent\",\"AnimationTimeline\",\"CSSAnimation\",\"CSSTransition\",\"DocumentTimeline\",\"BackgroundFetchManager\",\"BackgroundFetchRecord\",\"BackgroundFetchRegistration\",\"BluetoothUUID\",\"BrowserCaptureMediaStreamTrack\",\"CropTarget\",\"ContentVisibilityAutoStateChangeEvent\",\"DelegatedInkTrailPresenter\",\"Ink\",\"Highlight\",\"HighlightRegistry\",\"MathMLElement\",\"MediaMetadata\",\"MediaSession\",\"NavigatorUAData\",\"Notification\",\"PaymentManager\",\"PaymentRequestUpdateEvent\",\"PeriodicSyncManager\",\"PermissionStatus\",\"Permissions\",\"PushManager\",\"PushSubscription\",\"PushSubscriptionOptions\",\"RemotePlayback\",\"SharedWorker\",\"SpeechSynthesisErrorEvent\",\"SpeechSynthesisEvent\",\"SpeechSynthesisUtterance\",\"VideoPlaybackQuality\",\"ViewTransition\",\"webkitSpeechGrammar\",\"webkitSpeechGrammarList\",\"webkitSpeechRecognition\",\"webkitSpeechRecognitionError\",\"webkitSpeechRecognitionEvent\",\"n.getGamepads\",\"n.javaEnabled\",\"n.sendBeacon\",\"n.vibrate\",\"n.clearAppBadge\",\"n.getBattery\",\"n.getUserMedia\",\"n.requestMIDIAccess\",\"n.requestMediaKeySystemAccess\",\"n.setAppBadge\",\"n.webkitGetUserMedia\",\"n.getInstalledRelatedApps\",\"n.registerProtocolHandler\",\"n.unregisterProtocolHandler\",\"d.adoptNode\",\"d.append\",\"d.captureEvents\",\"d.caretRangeFromPoint\",\"d.clear\",\"d.close\",\"d.createAttribute\",\"d.createAttributeNS\",\"d.createCDATASection\",\"d.createComment\",\"d.createDocumentFragment\",\"d.createElement\",\"d.createElementNS\",\"d.createEvent\",\"d.createExpression\",\"d.createNSResolver\",\"d.createNodeIterator\",\"d.createProcessingInstruction\",\"d.createRange\",\"d.createTextNode\",\"d.createTreeWalker\",\"d.elementFromPoint\",\"d.elementsFromPoint\",\"d.evaluate\",\"d.execCommand\",\"d.exitFullscreen\",\"d.exitPictureInPicture\",\"d.exitPointerLock\",\"d.getElementById\",\"d.getElementsByClassName\",\"d.getElementsByName\",\"d.getElementsByTagName\",\"d.getElementsByTagNameNS\",\"d.getSelection\",\"d.hasFocus\",\"d.importNode\",\"d.open\",\"d.prepend\",\"d.queryCommandEnabled\",\"d.queryCommandIndeterm\",\"d.queryCommandState\",\"d.queryCommandSupported\",\"d.queryCommandValue\",\"d.querySelector\",\"d.querySelectorAll\",\"d.releaseEvents\",\"d.replaceChildren\",\"d.webkitCancelFullScreen\",\"d.webkitExitFullscreen\",\"d.write\",\"d.writeln\",\"d.getAnimations\",\"d.startViewTransition\",\"d.appendChild\",\"d.cloneNode\",\"d.compareDocumentPosition\",\"d.contains\",\"d.getRootNode\",\"d.hasChildNodes\",\"d.insertBefore\",\"d.isDefaultNamespace\",\"d.isEqualNode\",\"d.isSameNode\",\"d.lookupNamespaceURI\",\"d.lookupPrefix\",\"d.normalize\",\"d.removeChild\",\"d.replaceChild\",\"d.addEventListener\",\"d.dispatchEvent\",\"d.removeEventListener\"],\"D\":[\"Array\"],\"Infinity\":[\"Infinity\"],\"NaN\":[\"NaN\"],\"u\":[\"undefined\",\"event\"],\"Google Inc.\":[\"n.vendor\"],\"Mozilla\":[\"n.appCodeName\"],\"Netscape\":[\"n.appName\"],\"5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36\":[\"n.appVersion\"],\"MacIntel\":[\"n.platform\"],\"Gecko\":[\"n.product\"],\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36\":[\"n.userAgent\"],\"zh-CN\":[\"n.language\"],\"zh-CN,en,zh\":[\"n.languages\"],\"about:blank\":[\"d.URL\",\"d.documentURI\",\"d.referrer\"],\"BackCompat\":[\"d.compatMode\"],\"UTF-8\":[\"d.characterSet\",\"d.charset\",\"d.inputEncoding\"],\"text/html\":[\"d.contentType\"],\"ddys.art\":[\"d.domain\"],\"s\":[\"d.cookie\"],\"$time\":[\"d.lastModified\"],\"complete\":[\"d.readyState\"],\"off\":[\"d.designMode\"],\"visible\":[\"d.visibilityState\",\"d.webkitVisibilityState\"],\"\":[\"d.adoptedStyleSheets\"],\"#document\":[\"d.nodeName\"],\"https://ddys.art/celebrity-2023/?ep=8\":[\"d.baseURI\"]}"
    return encode(text) {
        "WZtz5BsO8wFbHKlcUYN-10MGoEugTVCrjPaSAfn6qDxh4QRdXI7epvi9J2yL+\$km3"[it]
    }
}
