import 'dart:convert';

class BandLevels {
  List<BandLevelModel>? bandLevels;

  BandLevels({this.bandLevels});

  factory BandLevels.fromJson(Map<String, dynamic> json) => BandLevels(
        bandLevels: (json['bandLevels'] as List<dynamic>?)
            ?.map((e) => BandLevelModel.fromJson(e as Map<String, dynamic>))
            .toList(),
      );

  Map<String, dynamic> toJson() => {
        'bandLevels': bandLevels?.map((e) => e.toJson()).toList(),
      };
}

class BandLevelModel {
  List<int>? bandFrequency;
  int bandLevel;
  int bandLevelId;

  BandLevelModel({
    this.bandFrequency,
    this.bandLevel = -1,
    this.bandLevelId = 0,
  });

  factory BandLevelModel.fromJson(Map<String, dynamic> json) => BandLevelModel(
        bandFrequency: (json['bandFrequency'] as List<dynamic>?)
            ?.map((e) => e as int)
            .toList(),
        bandLevel: json['bandLevel'] ?? -1,
        bandLevelId: json['bandLevelId'] ?? 0,
      );

  Map<String, dynamic> toJson() => {
        'bandFrequency': bandFrequency,
        'bandLevel': bandLevel,
        'bandLevelId': bandLevelId,
      };
}