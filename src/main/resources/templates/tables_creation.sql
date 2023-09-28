-- MySQL Script generated by MySQL Workbench
-- Sun Dec 11 12:17:10 2022
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema online_sales
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema online_sales
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `online_sales` ;
USE `online_sales` ;

-- -----------------------------------------------------
-- Table `online_sales`.`internet_markets`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `online_sales`.`internet_markets` ;

CREATE TABLE IF NOT EXISTS `online_sales`.`internet_markets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `internet_address` VARCHAR(512) NOT NULL,
  `is_delivery_paid` TINYINT NOT NULL,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `internet_address_UNIQUE` (`internet_address` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `online_sales`.`firm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `online_sales`.`firm` ;

CREATE TABLE IF NOT EXISTS `online_sales`.`firm` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(500) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `online_sales`.`goods`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `online_sales`.`goods` ;

CREATE TABLE IF NOT EXISTS `online_sales`.`goods` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(256) NOT NULL,
  `firm_id` BIGINT NOT NULL,
  `model` VARCHAR(256) NOT NULL,
  `tech_characteristics` VARCHAR(5000) NOT NULL DEFAULT '',
  `cost` INT UNSIGNED NOT NULL,
  `guarantee_term` INT UNSIGNED NOT NULL,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `firm_fk_idx` (`firm_id` ASC) VISIBLE,
  CONSTRAINT `firm_fk`
    FOREIGN KEY (`firm_id`)
    REFERENCES `online_sales`.`firm` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `online_sales`.`orders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `online_sales`.`orders` ;

CREATE TABLE IF NOT EXISTS `online_sales`.`orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `market_id` BIGINT NOT NULL,
  `goods_id` BIGINT NOT NULL,
  `order_date_time` DATETIME NOT NULL,
  `amount` INT UNSIGNED NOT NULL,
  `client_full_name` VARCHAR(256) NOT NULL,
  `telephone` VARCHAR(15) NOT NULL,
  `order_confirmation` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `market_fk_idx` (`market_id` ASC) VISIBLE,
  INDEX `goods_fk_idx` (`goods_id` ASC) VISIBLE,
  CONSTRAINT `market_fk`
    FOREIGN KEY (`market_id`)
    REFERENCES `online_sales`.`internet_markets` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `goods_fk`
    FOREIGN KEY (`goods_id`)
    REFERENCES `online_sales`.`goods` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `online_sales`.`delivery`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `online_sales`.`delivery` ;

CREATE TABLE IF NOT EXISTS `online_sales`.`delivery` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `delivery_date_time` DATETIME NOT NULL,
  `address` VARCHAR(2048) NOT NULL,
  `courier_full_name` VARCHAR(256) NOT NULL,
  `delivery_cost` INT UNSIGNED NOT NULL DEFAULT 0,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  INDEX `order_fk_idx` (`order_id` ASC) VISIBLE,
  UNIQUE INDEX `order_id_UNIQUE` (`order_id` ASC) VISIBLE,
  CONSTRAINT `order_fk`
    FOREIGN KEY (`order_id`)
    REFERENCES `online_sales`.`orders` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `online_sales`.`goods_offers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `online_sales`.`goods_offers` ;

CREATE TABLE IF NOT EXISTS `online_sales`.`goods_offers` (
  `market_id` BIGINT NOT NULL,
  `goods_id` BIGINT NOT NULL,
  INDEX `market_id_idx` (`market_id` ASC) VISIBLE,
  INDEX `goods_id_idx` (`goods_id` ASC) VISIBLE,
  CONSTRAINT `market_id`
    FOREIGN KEY (`market_id`)
    REFERENCES `online_sales`.`internet_markets` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `goods_id`
    FOREIGN KEY (`goods_id`)
    REFERENCES `online_sales`.`goods` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
