package com.hulk.aidb_demo.modelzoo
import com.hulk.aidb_demo.R


object ModelZoo{

    val models_md5 = mapOf(
        "onnx/yolov8/yolov8n.onnx" to "def4c122bb00bcfec3369aaac276f622",
        "onnx/yolov7/yolov7-tiny.onnx" to "03bac45904cf197964bec9463efd9b0c",
        "onnx/yolox/yolox_nano.onnx" to "61f78c84dddf7f12b3a7d945405978b6",
        "onnx/bisenet/face-parsing-BiSeNet-simplify.onnx" to "ae7080eadf00cfdcc8fca70ad51cdd9b",
        "onnx/ppocr/ch_ppocr_det_v3_sim.onnx" to "5495423b3f456bc7c2a4875a5027f802",
        "onnx/ppocr/ch_ppocr_mobile_v2.0_cls_sim.onnx" to "bb3047cdeb6b5d3f9df28d8f8eb82f0c",
        "onnx/ppocr/ch_ppocr_rec_v3_sim.onnx" to "26c381112d8f7d0300912c5b14402718",
        "onnx/pfpld/pfpld_kps98_simplify.onnx" to "db269d6a962f203ccc2581ab312f91e8",
        "onnx/3ddfav2/3ddfa_v2_mb05_bfm_head_base_simplify.onnx" to "0c4306d343e7588a82c2a4debb6b867f",
        "onnx/3ddfav2/3ddfa_v2_mb05_bfm_head_dense_simplify.onnx" to "fad98a1cd53f29759be16952ba775605",
        "onnx/mobilevit/MobileViT_XXS.onnx" to "c5d5bfdb14b0093c2d1f6956bf1885dd",
        "onnx/scrfd/scrfd_500m_kps_simplify.onnx" to "361cc5086b50122dcd543cab63093a96",
        "onnx/movenet/movenet-simplify.onnx" to "ad286482ba5243834a4d925c39a8e285",
        "paddle/yolov8/yolov8n.nb" to "447861676f0e1cef5c8df6e8ba881743",
        "paddle/yolov7/yolov7-tiny.nb" to "6a0a0034c103917ba14c3ace5d5fb26c",
        "paddle/yolox/yolox_nano.nb" to "729740bb6c9c6e3130fae3089b316666",
        "paddle/bisenet/face-parsing-BiSeNet.nb" to "5d99f94fc7b7dfe4e947640f10d86e62",
        "paddle/ppocr/ch_ppocr_mobile_v2.0_cls.nb" to "bd6712d00d63ab3fd992e7a065c542e8",
        "paddle/ppocr/ch_PP-OCRv3_det.nb" to "d1d2bc4f1c6c778a2f2bfc16d4d6cf52",
        "paddle/ppocr/ch_PP-OCRv3_rec.nb" to "88235104461fc04770831f3dd2fa7ed4",
        "paddle/pfpld/pfpld.nb" to "2cd82e03263d5faf18809f9b8f45aaf8",
        "paddle/3ddfav2/3ddfa_v2_mb05_bfm_head_base.nb" to "32b2fe41891ecc2ee73d5e2d570cf59a",
        "paddle/3ddfav2/3ddfa_v2_mb05_bfm_head_dense.nb" to "14d0d9c56c5148a65ada8bb1acde3c0f",
        "paddle/scrfd/scrfd_500m_kps.nb" to "b148eb6a034ca84782f6ae6f730ae409",
        "paddle/movenet/movenet.nb" to "67edc91029a795e3ddd1b67ce1023208",
        "tnn/yolov8/yolov8n.tnnmodel" to "d56f0da95fd647fd3b5118e8a78d1887",
        "tnn/yolov8/yolov8n.tnnproto" to "dbcf3b281efd2b070b137df32ce6ce13",
        "tnn/yolov7/yolov7-tiny.opt.tnnproto" to "68f98437cf40aaaa19da4e95d76d37ea",
        "tnn/yolov7/yolov7-tiny.opt.tnnmodel" to "0145555ae633527d4d23deeb466bbc2e",
        "tnn/yolox/yolox_nano.opt.tnnmodel" to "9302e3245dbf9b24c3934359821cfb35",
        "tnn/yolox/yolox_nano.opt.tnnproto" to "a2fc53b1b212b3ecaabd745bdabdc914",
        "tnn/bisenet/face-parsing-BiSeNet.tnnproto" to "c6954acf2d2331256d84121f5257dd40",
        "tnn/bisenet/face-parsing-BiSeNet.tnnmodel" to "b6765051f930a6e4055836409535cd3b",
        "tnn/pfpld/pfpld_kps98_simplify.tnnproto" to "007cb3ae43fe5cfc5749b4914cdae648",
        "tnn/pfpld/pfpld_kps98_simplify.tnnmodel" to "f4abecef874ace855e3756e2a4985221",
        "tnn/3ddfav2/3ddfa_v2_mb05_bfm_head_base_simplify.opt.tnnmodel" to "8928639538171ce646d8acc3fb6cb112",
        "tnn/3ddfav2/3ddfa_v2_mb05_bfm_head_dense_simplify.opt.tnnproto" to "2e853609b6c7ac6eb6b94f7cdff13efb",
        "tnn/3ddfav2/3ddfa_v2_mb05_bfm_head_dense_simplify.opt.tnnmodel" to "3c11f513a0ae4a54144ef7b4ed6ac42a",
        "tnn/3ddfav2/3ddfa_v2_mb05_bfm_head_base_simplify.opt.tnnproto" to "49dd57198ae820c7389e3f6dd430867d",
        "tnn/scrfd/scrfd_500m_kps_simplify.tnnproto" to "89523884b60c3a98734dc4483e6b7c4c",
        "tnn/scrfd/scrfd_500m_kps_simplify.tnnmodel" to "6461d915f48074b9af6b65d4e7d7509d",
        "tnn/movenet/movenet-simplify.tnnproto" to "512863dfe02b149d16c7eb9298df1986",
        "tnn/movenet/movenet-simplify.tnnmodel" to "f5765041b20e679691f0cd21fb0bf05b",
        "mnn/yolov8/yolov8n.mnn" to "9af8bd4dae5acb71a134f7419fecb496",
        "mnn/yolov7/yolov7-tiny.mnn" to "d4af940c8474aa6c55eab4f56fcce9ac",
        "mnn/yolox/yolox_nano.mnn" to "e1d702993daa0f7be48b71b63ec4a047",
        "mnn/bisenet/face-parsing-BiSeNet-simplify.mnn" to "fdd4932afef2093caaedc9a81ab5ba12",
        "mnn/ppocr/ch_ppocr_rec_v3_sim.mnn" to "b0a9b99fe00867262f70b335aff47c66",
        "mnn/ppocr/ch_ppocr_det_v3_sim.mnn" to "e84b030cea84dc2982d51810e80e91d8",
        "mnn/ppocr/ch_ppocr_mobile_v2.0_cls_sim.mnn" to "b2b3fe489570fc3d9db2250bde757c7b",
        "mnn/pfpld/pfpld_kps98_simplify.mnn" to "89d8d4f84815bb24598009289de08f81",
        "mnn/3ddfav2/3ddfa_v2_mb05_bfm_head_base_simplify.mnn" to "b46615ae8bdf87b50a847e7798ee5bba",
        "mnn/3ddfav2/3ddfa_v2_mb05_bfm_head_dense_simplify.mnn" to "ee1f1a9dc5ccbb659489c39bb84ace58",
        "mnn/mobilevit/MobileViT_XXS.mnn" to "7374e9d6fbae2dfa1ba1f1fb4da2dd06",
        "mnn/scrfd/scrfd_500m_kps_simplify.mnn" to "bd8d7d3a8ca760d27247250abd6c3ed3",
        "mnn/movenet/movenet-simplify.mnn" to "41d3097aaa0b20e3ccab0d0d64d264a2",
        "ncnn/yolov8/yolov8n.bin" to "e3bbc73c51899d4a60f2cfddca1048d0",
        "ncnn/yolov8/yolov8n.param" to "a6ef2296f2c3bc9ce3e926f0eee157c7",
        "ncnn/yolov7/yolov7-tiny.param" to "f5931865c92ccd24996a29200851db60",
        "ncnn/yolov7/yolov7-tiny.bin" to "51b6ff77d0b4f486812db7041cd3715a",
        "ncnn/yolox/yolox_nano.bin" to "f35e3677934ab66fc398414beccd4d70",
        "ncnn/yolox/yolox_nano.param" to "7595e528ab2b3b8e701b83da24229ed9",
        "ncnn/bisenet/face-parsing-BiSeNet-simplify.bin" to "cb1b804521fb3520e40778e4db3532ea",
        "ncnn/bisenet/face-parsing-BiSeNet-simplify.param" to "57e5eb9818df34d7230dd2267eddcf5b",
        "ncnn/ppocr/ch_ppocr_mobile_v2.0_cls_sim.bin" to "03423cf24c504cc5b5eccdb6c68d9a15",
        "ncnn/ppocr/ch_ppocr_mobile_v2.0_cls_sim.param" to "f8f4e1256e4b1cc7e8a5e2c960f31b1f",
        "ncnn/ppocr/ch_ppocr_rec_v3_sim.param" to "ef2f4c1fb31a14abf3539d4c38aa606e",
        "ncnn/ppocr/ch_ppocr_det_v3_sim.param" to "557b579a545fa9c5ed1b9ac0f6f599e7",
        "ncnn/ppocr/ch_ppocr_det_v3_sim.bin" to "37fdab47c1a7d4e82626b1f56f054853",
        "ncnn/ppocr/ch_ppocr_rec_v3_sim.bin" to "0be80f8c820d4851da2e40cf8996b18e",
        "ncnn/pfpld/pfpld_kps98_simplify.bin" to "5679605c09da02548579db2ca9681d9f",
        "ncnn/pfpld/pfpld_kps98_simplify.param" to "2af2ead6926725795091e4ff5d4773c4",
        "ncnn/3ddfav2/3ddfa_v2_mb05_bfm_head_dense_simplify.bin" to "e8009aeef82813212a01799948893fd4",
        "ncnn/3ddfav2/3ddfa_v2_mb05_bfm_head_base_simplify.param" to "27494b856719090bd1293723c55a9aab",
        "ncnn/3ddfav2/3ddfa_v2_mb05_bfm_head_base_simplify.bin" to "9ef29503a383a2adf539a65b3dd48be6",
        "ncnn/3ddfav2/3ddfa_v2_mb05_bfm_head_dense_simplify.param" to "343b30078982072ef68b169a528b82dd",
        "ncnn/scrfd/scrfd_500m_kps_simplify.param" to "19c86968ea94824ca9153d5e7fd937d7",
        "ncnn/scrfd/scrfd_500m_kps_simplify.bin" to "765468e615f52959195a828893d17cda",
        "ncnn/movenet/movenet-simplify.param" to "4bc7d64091218965704b343b4e870a37",
        "ncnn/movenet/movenet-simplify.bin" to "59b74e580ddf4ffe08980a4807bb90a5"
    )

    val backend = mapOf(
        1 to "Onnx",
        2 to "MNN",
        3 to "NCNN",
        4 to "TNN",
        5 to "OpenVINO",
        6 to "PaddleLite",

    )
    private val models = listOf(
        Model(0, "SCRFD", R.drawable.scrfd),
        Model(1, "PFPLD", R.drawable.pfpld),
        Model(2, "3DDFA-Base", R.drawable.tddfa_base),
        Model(3, "3DDFA-Dense", R.drawable.tddfa_dense),
        Model(4, "BiSeNet", R.drawable.bisnet),
        Model(5, "MoveNet", R.drawable.movenet),
        Model(6, "YoloX", R.drawable.yolox),
        Model(7, "YoloV7", R.drawable.yolov7),
        Model(8, "YoloV8", R.drawable.yolov8),
        Model(9, "MobileVit", R.drawable.mobilevit),
        Model(10, "PPOCR", R.drawable.ppocr))

//                ONNX = 1,
//                MNN = 2,
//                NCNN = 3,
//                TNN = 4,
//                OPENVINO = 5,
//                PADDLE_LITE = 6,
    // model:backend-list
    private val backend_support = mapOf(
    0 to listOf(1, 2, 3, 4, 6),
    1 to listOf(1, 2, 3, 4, 6),
    2 to listOf(1, 2, 3, 4, 6),
    3 to listOf(1, 2, 3, 4, 6),
    4 to listOf(1, 2, 3, 4, 6),
    5 to listOf(1, 2, 3, 4, 6),
    6 to listOf(1, 2, 3, 4, 6),
    7 to listOf(1, 2, 3, 4, 6),
    8 to listOf(1, 2, 3, 4, 6),
    9 to listOf(1, 2),
    10 to listOf(1, 2, 3, 6)
    )

    fun getData(): List<Model> {
        return models
    }

    fun getMap(): Map<Int, List<Int>> {
        return backend_support
    }

    fun backendID(backend: String): Int{
        return ModelZoo.backend.entries.find { it.value == backend }?.key!!
    }

}